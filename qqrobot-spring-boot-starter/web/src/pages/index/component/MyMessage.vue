<template>
    <div style="text-align: right;">
        <div class="left">
            <span class="label">{{ name }}</span>
            <div class="content">
                <slot></slot>
            </div>
            <div class="triangle-containter">
                <div class="triangle"></div>
            </div>
        </div>
        <el-avatar class="avatar" shape="circle" src="./static/avatar.png" />
    </div>
</template>

<script>
export default {
    name: "MyMessage",
    props: {
        name: ''
    }
}
</script>

<style scoped>
.left {
    display: inline-block;
    vertical-align: top;
    margin-right: 0.9em;
    position: relative;
}

.content {
    max-width: 20em;
    background-color: rgb(149, 239, 105);
    border-radius: 4px;
    padding: 0.5em 0.8em;
    word-wrap: break-word;
    margin-top: 0.2em;
    min-height: 1em;
}

/*noinspection CssUnknownProperty*/
.triangle-containter {
    position: absolute;
    top: 1.7em;
    right: -1.73em;
    -webkit-transform: rotate(-20deg);
}

.triangle {
    width: 0;
    height: 0;
    border-top: 0.3em solid transparent;
    border-left: 1em solid rgb(149, 239, 105);
    border-right: 1em solid transparent;
    border-bottom: 0.3em solid transparent;
}
</style>
