<template>
	<div>
		<el-form :model="form" :rules="formRules" ref="formRef" label-position="top">
			<el-row :gutter="20">
				<el-col :span="12">
					<el-form-item label="文章标题" prop="title">
						<el-input v-model="form.title" placeholder="请输入标题"></el-input>
					</el-form-item>
				</el-col>
				<el-col :span="12">
					<el-form-item label="文章首图URL" prop="firstPicture">
						<el-input v-model="form.firstPicture" placeholder="文章首图，用于随机文章展示"></el-input>
					</el-form-item>
				</el-col>
			</el-row>

			<el-form-item label="文章描述" prop="description">
				<mavon-editor v-model="form.description" />
				<!-- 添加AI生成按钮 -->
				<el-button type="primary" icon="el-icon-magic-stick" size="mini" @click="generateDescription"
					style="margin-bottom: 10px;">
					AI 生成描述
				</el-button>
			</el-form-item>

			<el-form-item label="文章正文" prop="content">

				<mavon-editor v-model="form.content" />
				<el-button type="primary" icon="el-icon-magic-stick" size="mini" @click="handleAiPolish"
					style="margin-bottom: 10px;">
					AI 润色
				</el-button>
			</el-form-item>

			<el-row :gutter="20">
				<el-col :span="12">
					<el-form-item label="分类" prop="cate">
						<el-select v-model="form.cate" placeholder="请选择分类（输入可添加新分类）" :allow-create="true"
							:filterable="true" style="width: 100%;">
							<el-option :label="item.name" :value="item.id" v-for="item in categoryList"
								:key="item.id"></el-option>
						</el-select>
					</el-form-item>
				</el-col>

				<!-- <el-col :span="12">
					<el-form-item label="标签" prop="tagList">
						<el-select v-model="form.tagList" placeholder="请选择标签（输入可添加新标签）" :allow-create="true" :filterable="true" :multiple="true" style="width: 100%;">
							<el-option :label="item.name" :value="item.id" v-for="item in tagList" :key="item.id"></el-option>
						</el-select>
					</el-form-item>
				</el-col> -->

				<!-- 标签选择区域 -->
				<el-col :span="12">
					<el-form-item label="标签" prop="tagList">
						<!-- 原有标签选择器 -->
						<el-select v-model="form.tagList" placeholder="请选择标签（输入可添加新标签）" :allow-create="true"
							:filterable="true" :multiple="true" style="width: 100%;">
							<el-option :label="item.name" :value="item.id" v-for="item in tagList"
								:key="item.id"></el-option>
						</el-select>

						<el-button type="primary" icon="el-icon-lightbulb" size="mini" @click="getAiRecommendTags"
							style="margin-bottom: 10px;">
							AI 推荐标签
						</el-button>

						<!-- AI推荐标签展示区域 -->
						<div v-if="aiTags.length > 0"
							style="margin-top: 10px; padding: 10px; background: #f5f7fa; border-radius: 4px;">
							<span style="font-size: 12px; color: #666;">AI推荐标签：</span>
							<el-checkbox-group v-model="selectedAiTags" style="margin-top: 5px;">
								<el-checkbox v-for="tag in aiTags" :key="tag.name" :label="tag.id || tag.name"
									:disabled="isTagSelected(tag)" style="margin-right: 10px;">
									{{ tag.name }}
									<span v-if="!tag.exists" style="color: #409EFF; font-size: 12px;">(新标签)</span>
								</el-checkbox>
							</el-checkbox-group>

							<el-button type="text" size="mini" @click="addSelectedTags"
								:disabled="selectedAiTags.length === 0" style="margin-left: 10px;">
								添加选中标签
							</el-button>
						</div>
					</el-form-item>
				</el-col>

			</el-row>

			<el-row :gutter="20">
				<el-col :span="8">
					<el-form-item label="字数" prop="words">
						<el-input v-model="form.words" type="number"></el-input>
					</el-form-item>
				</el-col>
				<el-col :span="8">
					<el-form-item label="阅读时长(分钟)" prop="readTime">
						<el-input v-model="form.readTime" type="number"></el-input>
					</el-form-item>
				</el-col>
				<el-col :span="8">
					<el-form-item label="浏览次数" prop="views">
						<el-input v-model="form.views" type="number"></el-input>
					</el-form-item>
				</el-col>
			</el-row>

			<el-form-item style="text-align: right;">
				<el-button type="primary" @click="dialogVisible = true">保存</el-button>
			</el-form-item>
		</el-form>

		<!--编辑可见性状态对话框-->
		<el-dialog title="博客可见性" width="30%" :visible.sync="dialogVisible">
			<!--内容主体-->
			<el-form label-width="50px" @submit.native.prevent>
				<el-form-item>
					<el-radio-group v-model="radio">
						<el-radio :label="1">公开</el-radio>
						<el-radio :label="2">私密</el-radio>
						<el-radio :label="3">密码保护</el-radio>
					</el-radio-group>
				</el-form-item>
				<el-form-item label="密码" v-if="radio === 3">
					<el-input v-model="form.password"></el-input>
				</el-form-item>
				<el-form-item v-if="radio !== 2">
					<el-row>
						<el-col :span="6">
							<el-switch v-model="form.appreciation" active-text="赞赏"></el-switch>
						</el-col>
						<el-col :span="6">
							<el-switch v-model="form.recommend" active-text="推荐"></el-switch>
						</el-col>
						<el-col :span="6">
							<el-switch v-model="form.commentEnabled" active-text="评论"></el-switch>
						</el-col>
						<el-col :span="6">
							<el-switch v-model="form.top" active-text="置顶"></el-switch>
						</el-col>
					</el-row>
				</el-form-item>
			</el-form>
			<!--底部-->
			<span slot="footer">
				<el-button @click="dialogVisible = false">取 消</el-button>
				<el-button type="primary" @click="submit">保存</el-button>
			</span>
		</el-dialog>
	</div>
</template>

<script>
import Breadcrumb from "@/components/Breadcrumb";
// import { getCategoryAndTag, saveBlog, getBlogById, updateBlog } from '@/api/blog'
// 引入新接口
import { getCategoryAndTag, saveBlog, getBlogById, updateBlog, aiPolishContent, getRecommendTags } from '@/api/blog'
import { generateDescription } from '@/api/blog' // 引入接口方法

export default {
	name: "WriteBlog",
	components: { Breadcrumb },
	data() {
		return {
			categoryList: [],
			tagList: [],
			dialogVisible: false,
			radio: 1,
			form: {
				title: '',
				firstPicture: '',
				description: '',
				content: '',
				cate: null,
				tagList: [],
				words: null,
				readTime: null,
				views: 0,
				appreciation: false,
				recommend: false,
				commentEnabled: false,
				top: false,
				published: false,
				password: '',
			},
			formRules: {
				title: [{ required: true, message: '请输入标题', trigger: 'change' }],
				firstPicture: [{ required: true, message: '请输入首图链接', trigger: 'change' }],
				cate: [{ required: true, message: '请选择分类', trigger: 'change' }],
				tagList: [{ required: true, message: '请选择标签', trigger: 'change' }],
				words: [{ required: true, message: '请输入文章字数', trigger: 'change' }],
			},
			aiTags: [],
			selectedAiTags: []
		}
	},
	watch: {
		'form.words'(newValue) {
			this.form.readTime = newValue ? Math.max(1, Math.round(newValue / 300)) : null;
		},
		'form.content'(newValue) {
			this.form.words = newValue ? (newValue.replace(/<.*?>/g, '').replace(/https?:\/\/\S+/g, '')).length : 0
		}
	},
	created() {
		this.getData()
		if (this.$route.params.id) {
			this.getBlog(this.$route.params.id)
		}
	},
	methods: {
		getData() {
			getCategoryAndTag().then(res => {
				this.categoryList = res.data.categories
				this.tagList = res.data.tags
			})
		},
		getBlog(id) {
			getBlogById(id).then(res => {
				this.computeCategoryAndTag(res.data)
				this.form = res.data
				this.radio = this.form.published ? (this.form.password !== '' ? 3 : 1) : 2
			})
		},
		computeCategoryAndTag(blog) {
			blog.cate = blog.category.id
			blog.tagList = []
			blog.tags.forEach(item => {
				blog.tagList.push(item.id)
			})
		},
		submit() {
			if (this.radio === 3 && (this.form.password === '' || this.form.password === null)) {
				return this.msgError("密码保护模式必须填写密码！")
			}
			this.$refs.formRef.validate(valid => {
				if (valid) {
					if (this.radio === 2) {
						this.form.appreciation = false
						this.form.recommend = false
						this.form.commentEnabled = false
						this.form.top = false
						this.form.published = false
					} else {
						this.form.published = true
					}
					if (this.radio !== 3) {
						this.form.password = ''
					}
					if (this.$route.params.id) {
						this.form.category = null
						this.form.tags = null
						updateBlog(this.form).then(res => {
							this.msgSuccess(res.msg)
							this.$router.push('/blog/list')
						})
					} else {
						saveBlog(this.form).then(res => {
							this.msgSuccess(res.msg)
							this.$router.push('/blog/list')
						})
					}
				} else {
					this.dialogVisible = false
					return this.msgError('请填写必要的表单项')
				}
			})
		},
		async handleAiPolish() {
			if (!this.form.content.trim()) {
				return this.$message.warning("请先输入文章内容");
			}

			const loadingInstance = this.$loading({
				lock: true,
				text: 'AI正在润色中，请稍候...',
				spinner: 'el-icon-loading',
				background: 'rgba(0, 0, 0, 0.3)'
			});

			try {
				const res = await aiPolishContent(this.form.content);
				loadingInstance.close();

				// 打印响应结果以便调试
				// console.log("润色接口返回结果：", res);

				if (res.code === 200) {
					// 从响应中获取润色后的内容（根据实际结构调整）
					const polishedContent = res.data || '';

					// 更新编辑器内容
					this.form.content = polishedContent;

					// 计算字数（先确保内容存在再调用replace，避免报错）
					this.form.words = polishedContent
						? polishedContent.replace(/<.*?>/g, '').replace(/https?:\/\/\S+/g, '').length
						: 0;

					this.$message.success("润色完成");
				} else {
					this.$message.error(res.msg || "润色失败");
				}
			} catch (error) {
				loadingInstance.close();
				this.$message.error("请求失败，请重试");
				console.error(error);
			}
		},

		// 判断标签是否已被选中
		isTagSelected(tag) {
			if (tag.exists) {
				// 已存在的标签判断id是否在表单中
				return this.form.tagList.includes(tag.id)
			} else {
				// 新标签判断name是否在表单的标签名称中
				const selectedTagNames = this.tagList
					.filter(item => this.form.tagList.includes(item.id))
					.map(item => item.name)
				return selectedTagNames.includes(tag.name)
			}
		},

		// 获取AI推荐标签
		async getAiRecommendTags() {
			if (!this.form.content.trim()) {
				return this.$message.warning("请先输入文章内容再获取推荐标签");
			}

			// 1. 将 loading 提升到 try 外部定义，确保 catch 能访问
			let loading = null;
			try {
				// 2. 这里赋值给外部定义的 loading 变量
				loading = this.$loading({
					lock: true,
					text: 'AI正在分析内容并推荐标签...',
					spinner: 'el-icon-loading',
					background: 'rgba(0, 0, 0, 0.7)'
				});

				const res = await getRecommendTags(this.form.content);
				loading.close(); // 正常流程关闭加载

				if (res.code === 200) {
					this.aiTags = res.data || [];
					this.selectedAiTags = [];
					if (this.aiTags.length === 0) {
						this.$message.info("未找到合适的推荐标签");
					} else {
						this.$message.success(`成功推荐 ${this.aiTags.length} 个标签`);
					}
				} else {
					this.$message.error(res.msg || "获取推荐标签失败");
				}
			} catch (error) {
				// 3. 此时 loading 是外部变量，可正常访问（未初始化时为 null，不会报错）
				if (loading) loading.close();
				console.error("标签推荐接口异常:", error);
				this.$message.error("网络异常，请稍后重试");
			}
		},
		// 添加选中的AI标签到表单
		addSelectedTags() {
			if (this.selectedAiTags.length === 0) return;

			const newTagList = [...this.form.tagList]; // 复制原表单标签列表

			this.selectedAiTags.forEach(tagKey => {
				const tagInfo = this.aiTags.find(tag =>
					(tag.id && tag.id === tagKey) || tag.name === tagKey
				);
				if (!tagInfo) return; // 防止无效 tagKey，直接跳过

				if (tagInfo.exists && tagInfo.id) {
					// 已存在的标签：判断 ID 是否已在 newTagList 中，避免重复
					if (!newTagList.includes(tagInfo.id)) {
						newTagList.push(tagInfo.id);
					}
				} else {
					// 新标签：双重去重（tagList 中无同名 + newTagList 中无同名）
					const isNameInTagList = this.tagList.some(t => t.name === tagInfo.name);
					const isNameInNewTagList = newTagList.includes(tagInfo.name);

					if (!isNameInTagList && !isNameInNewTagList) {
						// 生成更可靠的临时 ID（用 UUID 格式，需确保项目有 UUID 工具或手动模拟）
						const tempId = `temp_${Date.now()}_${Math.floor(Math.random() * 10000)}`;
						this.tagList.push({ id: tempId, name: tagInfo.name });
						newTagList.push(tagInfo.name); // 新标签用 name 存入表单
					}
				}
			});

			this.form.tagList = newTagList;
			this.aiTags = [];
			this.selectedAiTags = [];
			this.$message.success("标签添加成功");
		},

		async generateDescription() {
			// 校验正文是否为空（提前校验，避免创建不必要的 loading）
			if (!this.form.content.trim()) {
				return this.msgWarning("请先输入文章正文再生成描述");
			}

			// 1. 定义 loading 变量（提升作用域，确保 finally 能访问）
			let loading = null;
			try {
				// 2. 创建 loading 实例（在 try 内创建，避免校验不通过时创建无效实例）
				loading = this.$loading({
					lock: true,
					text: 'AI正在生成文章描述...',
					spinner: 'el-icon-loading'
				});

				// 调用后端接口
				const res = await generateDescription(this.form.content);

				// 接口调用成功后的业务逻辑
				if (res.code === 200 && res.data) {
					this.form.description = res.data;
					this.msgSuccess("描述生成成功");
				} else {
					this.msgError(res.msg || "生成描述失败");
				}
			} catch (error) {
				// 捕获接口调用异常
				console.error("生成描述接口异常:", error);
				this.msgError("网络异常，请稍后重试");
			} finally {
				// 3. 关键：无论成功/失败，finally 都会执行，确保 loading 必关闭
				if (loading) { // 加判断：避免 loading 未创建时调用 close()
					loading.close();
				}
			}
		}

	}
}
</script>

<style scoped></style>