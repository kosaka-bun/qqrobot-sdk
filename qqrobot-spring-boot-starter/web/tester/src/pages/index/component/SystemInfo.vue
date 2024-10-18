<template>
    <div style="text-align: center;">
        <span class="info">
            <slot></slot>
        </span>
    </div>
</template>

<script>
export default {
    name: 'SystemInfo'
}
</script>

<style scoped>
.info {
    color: gray;
    font-size: 0.8em;
}
</style>
