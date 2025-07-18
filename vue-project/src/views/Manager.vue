<template>
    <div>
        <!-- 头部开始 -->
        <div class="header">
            <h1>后台管理系统</h1>
        </div>
        <!-- 头部结束 -->

        <!-- 主体开始 -->
        <div class="body">
            <!-- 左侧菜单栏 -->
            <div class="menu">
                <el-menu class="el-menu" router :default-active="router.currentRoute.value.path">
                    <el-menu-item index="/manager/home"><el-icon>
                            <House />
                        </el-icon>系统首页</el-menu-item>
                    <el-sub-menu index="">
                        <template #title>
                            <el-icon>
                                <User />
                            </el-icon>
                            <span>用户管理</span>
                        </template>
                        <el-menu-item index="">管理员信息</el-menu-item>
                        <el-menu-item index="/manager/employee">员工信息</el-menu-item>
                    </el-sub-menu>
                    <el-menu-item index="/manager/data"><el-icon>
                            <DataAnalysis />
                        </el-icon>数据展示</el-menu-item>
                    <el-menu-item index="/manager/personal"><el-icon>
                            <UserFilled />
                        </el-icon>个人信息</el-menu-item>
                    <el-menu-item index=""><el-icon>
                            <SwitchButton />
                        </el-icon>退出系统</el-menu-item>
                </el-menu>
            </div>
            <!-- 右侧数据展示区 -->
            <div class="data">
                <!-- 这里添加右侧数据展示的内容 -->
                <RouterView></RouterView>
            </div>
        </div>
        <!-- 主体结束 -->
    </div>
</template>

<script setup>
import router from '@/router';
import { ElMenu, ElMenuItem, ElIcon, ElSubMenu } from 'element-plus';
import { House, DataAnalysis, User, UserFilled, SwitchButton } from '@Element-plus/icons-vue';

// 示例方法

</script>

<style scoped>
.header {
    height: 60px;
    background-color: #70abf8;
    margin: 0px;
    padding: 0px;
    /* 确保没有默认的内边距 */
}

.body {
    display: flex;
    height: calc(100vh - 60px);
    /* 确保主体内容占满剩余高度 */
}

.menu {
    width: 200px;
    border-right: 1px solid #2a2120;
    min-height: 100%;
    /* 确保菜单栏占满主体高度 */
}

.el-menu {
    border: 0px;
}

.el-menu .is-active {
    background-color: #e6ecf7 !important;
}

.data {
    flex: 1;
    background-color: #edeaf8;
    padding: 10px;
}
</style>
