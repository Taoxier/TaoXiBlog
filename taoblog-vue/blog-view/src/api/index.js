import axios from '@/plugins/axios'

export function getHitokoto() {
	return axios({
		url: 'https://v1.hitokoto.cn/?c=i&a&d*b',
		method: 'GET'
	})
}

export function getSite() {
	return axios({
		url: 'site',
		method: 'GET'
	})
}