export default {
    isEmptyObject(obj) {
        let count = 0;
        for(let key in obj) {
            count++;
        }
        return count === 0;
    }
}
