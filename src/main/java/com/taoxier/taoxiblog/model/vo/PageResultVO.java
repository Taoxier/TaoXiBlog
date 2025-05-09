package com.taoxier.taoxiblog.model.vo;

import lombok.*;

import java.util.List;

/**
 * @Description ：分页结果
 * @Author taoxier
 * @Date 2025/4/25
 */
@Data
@NoArgsConstructor
public class PageResultVO<T> {
	private Integer totalPage;//总页数
	private List<T> list;//数据

	public PageResultVO(Integer totalPage, List<T> list) {
		this.totalPage = totalPage;
		this.list = list;
	}
}
