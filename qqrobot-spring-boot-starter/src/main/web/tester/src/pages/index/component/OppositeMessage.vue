<template>
    <div>
        <el-avatar class="avatar" shape="circle" src="./static/avatar.png" />
        <div class="right">
            <span class="label">{{ name }}</span>
            <div class="content">
                <slot></slot>
            </div>
            <div class="triangle-containter">
                <div class="triangle"></div>
                <div class="triangle-cover"></div>
            </div>
            <div class="triangle-cover2"></div>
        </div>
    </div>
</template>

<script>
export default {
    name: 'OppositeMessage',
    props: {
        name: ''
    }
}
</script>

<style scoped>
.right {
    display: inline-block;
    vertical-align: top;
    margin-left: 0.9em;
    position: relative;
}

.content {
    max-width: 20em;
    border: 1px solid var(--el-border-color);
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
    left: -1.73em;
    -webkit-transform: rotate(20deg);
}

.triangle {
    width: 0;
    height: 0;
    border-top: 0.3em solid transparent;
    border-left: 1em solid transparent;
    border-right: 1em solid var(--el-border-color);
    border-bottom: 0.3em solid transparent;
}

.triangle-cover {
    width: 0;
    height: 0;
    border-top: 0.3em solid transparent;
    border-left: 1em solid transparent;
    border-right: 1em solid white;
    border-bottom: 0.3em solid transparent;
    position: absolute;
    top: 0;
    left: 0.15em;
}

.triangle-cover2 {
    width: 0.5em;
    height: 1em;
    background-color: white;
    position: absolute;
    top: 1.8em;
    left: 0.05em;
}
</style>
