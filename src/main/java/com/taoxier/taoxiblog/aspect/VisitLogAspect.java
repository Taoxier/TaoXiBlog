package com.taoxier.taoxiblog.aspect;

import com.taoxier.taoxiblog.annotation.VisitLogger;
import com.taoxier.taoxiblog.constant.RedisKeyConstants;
import com.taoxier.taoxiblog.enums.VisitBehavior;
import com.taoxier.taoxiblog.model.dto.VisitLogRemarkDTO;
import com.taoxier.taoxiblog.model.entity.VisitLog;
import com.taoxier.taoxiblog.model.entity.Visitor;
import com.taoxier.taoxiblog.model.vo.BlogDetailVO;
import com.taoxier.taoxiblog.model.vo.ResultVO;
import com.taoxier.taoxiblog.service.RedisService;
import com.taoxier.taoxiblog.service.VisitLogService;
import com.taoxier.taoxiblog.service.VisitorService;
import com.taoxier.taoxiblog.util.AopUtils;
import com.taoxier.taoxiblog.util.IpAddressUtils;
import com.taoxier.taoxiblog.util.JacksonUtils;
import com.taoxier.taoxiblog.util.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Calendar;
import java.util.Map;
import java.util.UUID;

/**
 * @Author taoxier
 * @Date
 * @描述 AOP记录访问日志 （记录用户的访问信息，同时会对访客进行标识码校验）
 */
@Component
@Aspect
public class VisitLogAspect {
    @Autowired
    VisitLogService visitLogService;
    @Autowired
    VisitorService visitorService;
    @Autowired
    RedisService redisService;

    ThreadLocal<Long> currentTime = new ThreadLocal<>();

    /**
     * 配置切入点
     */
    @Pointcut("@annotation(visitLogger)")
    public void logPointcut(VisitLogger visitLogger) {
    }

    /**
    * @描述  配置环绕通知
    * @param joinPoint
    * @param visitLogger
    * @return Object
    * @Author taoxier
    */
    @Around("logPointcut(visitLogger)")
    public Object logAround(ProceedingJoinPoint joinPoint, VisitLogger visitLogger) throws Throwable {
        currentTime.set(System.currentTimeMillis());

        //执行目标方法
        ResultVO result = (ResultVO) joinPoint.proceed();

        int times = (int) (System.currentTimeMillis() - currentTime.get());
        currentTime.remove();

        //获取请求对象
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        //校验访客标识码
        String identification = checkIdentification(request);
        //记录访问日志
        VisitLog visitLog = handleLog(joinPoint, visitLogger, request, result, times, identification);
//        System.out.println("切面——visitLog内容："+ visitLog);
        visitLogService.saveVisitLog(visitLog);
        return result;
    }

    /**
    * @描述  校验访客标识码
    * @param request
    * @return String
    * @Author taoxier
    */
    private String checkIdentification(HttpServletRequest request) {
        String identification = request.getHeader("identification");
        if (identification == null) {
            //请求头没有uuid，签发uuid并保存到数据库和Redis
            identification = saveUUID(request);
        } else {
            //校验Redis中是否存在uuid
            boolean redisHas = redisService.hasValueInSet(RedisKeyConstants.IDENTIFICATION_SET, identification);
            //Redis中不存在uuid
            if (!redisHas) {
                //校验数据库中是否存在uuid
                boolean mysqlHas = visitorService.hasUUID(identification);
                if (mysqlHas) {
                    //数据库存在，保存至Redis
                    redisService.saveValueToSet(RedisKeyConstants.IDENTIFICATION_SET, identification);
                } else {
                    //数据库不存在，签发新的uuid
                    identification = saveUUID(request);
                }
            }
        }
        return identification;
    }

    /**
    * @描述  签发UUID，并保存至数据库和Redis
    * @param request
    * @return String
    * @Author taoxier
    */
    private String saveUUID(HttpServletRequest request) {
        //获取响应对象
        HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
        //获取当前时间戳，精确到小时，防刷访客数据
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        String timestamp = Long.toString(calendar.getTimeInMillis() / 1000);
        //获取访问者基本信息
        String ip = IpAddressUtils.getIpAddress(request);
        String userAgent = request.getHeader("User-Agent");
        //根据时间戳、ip、userAgent生成UUID
        String nameUUID = timestamp + ip + userAgent;
        String uuid = UUID.nameUUIDFromBytes(nameUUID.getBytes()).toString();
        //添加访客标识码UUID至响应头
        response.addHeader("identification", uuid);
        //暴露自定义header供页面资源使用
        response.addHeader("Access-Control-Expose-Headers", "identification");
        //校验Redis中是否存在uuid
        boolean redisHas = redisService.hasValueInSet(RedisKeyConstants.IDENTIFICATION_SET, uuid);
        if (!redisHas) {
            //保存至Redis
            redisService.saveValueToSet(RedisKeyConstants.IDENTIFICATION_SET, uuid);
            //保存至数据库
            Visitor visitor = new Visitor(uuid, ip, userAgent);
            visitorService.saveVisitor(visitor);
        }
        return uuid;
    }

    /**
    * @描述  设置VisitLogger对象属性
    * @param joinPoint
    * @param visitLogger
    * @param request
    * @param result
    * @param times
    * @param identification
    * @return VisitLog
    * @Author taoxier
    */
    private VisitLog handleLog(ProceedingJoinPoint joinPoint, VisitLogger visitLogger, HttpServletRequest request, ResultVO result, int times, String identification) {
        String uri = request.getRequestURI();
        String method = request.getMethod();
        String ip = IpAddressUtils.getIpAddress(request);
        String userAgent = request.getHeader("User-Agent");
        Map<String, Object> requestParams = AopUtils.getRequestParams(joinPoint);
        VisitLogRemarkDTO visitLogRemark = judgeBehavior(visitLogger.value(), requestParams, result);
        VisitLog log = new VisitLog(identification, uri, method, visitLogger.value().getBehavior(),
                visitLogRemark.getContent(), visitLogRemark.getRemark(), ip, times, userAgent);
        try {
            // 将请求参数转换为 JSON 字符串
            String paramsJson = JacksonUtils.writeValueAsString(requestParams);
            // 检查字符串长度，如果长度小于 2000 则截取到字符串末尾
            int endIndex = Math.min(paramsJson.length(), 2000);
            log.setParam(paramsJson.substring(0, endIndex));
        } catch (Exception e) {
            // 处理 JSON 转换异常
            log.setParam("Failed to convert request params to JSON");
        }
        return log;
    }

    /**
    * @描述  根据访问行为，设置对应的访问内容或备注
    * @param behavior
    * @param requestParams
    * @param result
    * @return VisitLogRemarkDTO
    * @Author taoxier
    */
    private VisitLogRemarkDTO judgeBehavior(VisitBehavior behavior, Map<String, Object> requestParams, ResultVO result) {
        String remark = "";
        String content = behavior.getContent();
        switch (behavior) {
            case INDEX:
            case MOMENT:
                remark = "第" + requestParams.get("pageNum") + "页";
                break;
            case BLOG:
                if (result.getCode() == 200) {
                    BlogDetailVO blog = (BlogDetailVO) result.getData();
                    String title = blog.getTitle();
                    content = title;
                    remark = "文章标题：" + title;
                }
                break;
            case SEARCH:
                if (result != null && result.getCode() == 200) {
                    Object queryParam = requestParams.get("query");
                    String query = "";
                    if (queryParam instanceof String[]) {
                        String[] arr = (String[]) queryParam;
                        query = arr.length > 0 ? arr[0] : "";
                    } else if (queryParam instanceof String) {
                        query = (String) queryParam;
                    }
                    content = query;
                    remark = "搜索内容：" + query;
                }
                break;
            case CATEGORY:
                Object categoryParam = requestParams.get("categoryName");
                String categoryName = "";
                // 处理数组（服务器场景）和字符串（本地场景）
                if (categoryParam instanceof String[]) {
                    // 取数组第一个元素
                    String[] arr = (String[]) categoryParam;
                    categoryName = arr.length > 0 ? arr[0] : "";
                } else if (categoryParam instanceof String) {
                    categoryName = (String) categoryParam;
                }
                content = categoryName;
                remark = "分类名称：" + categoryName + "，第" + requestParams.get("pageNum") + "页";
                break;
            case TAG:
                Object tagParam = requestParams.get("tagName");
                String tagName = "";
                // 处理数组（服务器场景）和字符串（本地场景）
                if (tagParam instanceof String[]) {
                    String[] arr = (String[]) tagParam;
                    tagName = arr.length > 0 ? arr[0] : "";
                } else if (tagParam instanceof String) {
                    tagName = (String) tagParam;
                }
                content = tagName;
                remark = "标签名称：" + tagName + "，第" + requestParams.get("pageNum") + "页";
                break;
            case CLICK_FRIEND:
                String nickname = (String) requestParams.get("nickname");
                content = nickname;
                remark = "友链名称：" + nickname;
                break;
        }
        return new VisitLogRemarkDTO(content, remark);
    }
}
