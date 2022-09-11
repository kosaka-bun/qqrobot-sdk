import { ElMessage } from 'element-plus'

export default {
    alert(message, type) {
        ElMessage({
            message, type
        });
    },
    success(message) {
        this.alert(message, 'success');
    },
    error(message) {
        this.alert(message, 'error');
    },
    info(message) {
        this.alert(message, undefined);
    }
}
