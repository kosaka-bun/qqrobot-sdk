export default {
    isEmptyObject(obj) {
        let count = 0;
        for(let key in obj) {
            count++;
        }
        return count === 0;
    },
    getAbsoluteUrl(url) {
        let a = document.createElement('a');
        a.href = url;
        return a.href;
    },
    getWebsocketAbsoluteUrl(url) {
        url = this.getAbsoluteUrl(url);
        return url.replace(/https?:\/\//g, 'ws://');
    }
}
