import axios from '@/util/request'

export function getDataByQuery(queryInfo) {
	return axios({
		url: 'blogs',
		method: 'GET',
		params: {
			...queryInfo
		}
	})
}

export function deleteBlogById(id) {
	return axios({
		url: 'blog',
		method: 'DELETE',
		params: {
			id
		}
	})
}

export function getCategoryAndTag() {
	return axios({
		url: 'categoryAndTag',
		method: 'GET'
	})
}

export function saveBlog(blog) {
	return axios({
		url: 'blog',
		method: 'POST',
		data: {
			...blog
		}
	})
}

export function updateTop(id, top) {
	return axios({
		url: 'blog/top',
		method: 'PUT',
		params: {
			id,
			top
		}
	})
}

export function updateRecommend(id, recommend) {
	return axios({
		url: 'blog/recommend',
		method: 'PUT',
		params: {
			id,
			recommend
		}
	})
}

export function updateVisibility(id, form) {
	return axios({
		url: `blog/${id}/visibility`,
		method: 'PUT',
		data: {
			...form
		}
	})
}

export function getBlogById(id) {
	return axios({
		url: 'blog',
		method: 'GET',
		params: {
			id
		}
	})
}

export function updateBlog(blog) {
	return axios({
		url: 'blog',
		method: 'PUT',
		data: {
			...blog
		}
	})
}

// 在taoblog-vue/blog-cms/src/api/blog.js中添加
export function aiPolishContent(content) {
  return axios({
    url: '/blog/polish',
    method: 'POST',
    data: {
      content
	  },
	timeout: 100000
  })
}

export function getRecommendTags(content) {
	return axios({
		url: '/blog/recommend-tags',
		method: 'POST',
		data: {
			content
		},
		timeout: 100000
	})
}
	
	export function generateDescription(content) {
  return axios({
    url: '/blog/generate-description',
    method: 'POST',
    data: {
      content: content // 传递正文内容给后端
	  },
			timeout: 100000
  })
}


