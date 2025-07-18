<template>
    <!-- 搜索区域 -->
    <el-card class="card">
        <el-input class="input" v-model="data" placeholder="请输入数据" prefix-icon="Search"></el-input>
        <el-button type="primary" @click="search">搜索</el-button>
        <el-button type="warning" @click="search">重置</el-button>
    </el-card>
    <!-- 操作区域 -->
    <el-card class="card">
        <el-button type="primary" @click="search">添加</el-button>
        <el-button type="danger" @click="search">批量删除</el-button>
        <el-button type="info" @click="search">导入</el-button>
        <el-button type="success" @click="search">导出</el-button>
    </el-card>
    <!-- 数据展示区域 -->
    <el-card>
        <div>
            <el-table :data="tabledata" stripe>
                <el-table-column label="ID" prop="id" />
                <el-table-column label="姓名" prop="name" />
                <el-table-column label="年龄" prop="age" />
            </el-table>
            <!-- 分页器 -->
            <div class ="pagination">
                <el-pagination v-model:current-page="datamsg.pageNum" v-model:page-size="datamsg.pageSize"
                    :page-sizes="[5, 10, 15, 20]" background layout="total, sizes, prev, pager, next, jumper"
                    :total="datamsg.total" />
            </div>
        </div>
    </el-card>

</template>
<script setup>
import { ref } from 'vue';
import { Search } from '@Element-plus/icons-vue';

let data = ref("");

const tabledata = [
    { id: 1, name: "zhangsan", age: 18 },
    { id: 2, name: "lisi", age: 20 },
    { id: 3, name: "wangwu", age: 22 },
    { id: 4, name: "zhaoliu", age: 24 },
    { id: 5, name: "qianqi", age: 26 },
];
const datamsg={
    pageNum: 1,
    pageSize: 5,
    total: 5,
}

</script>
<style scoped>
.card {
    margin-bottom: 3px;
}

.input {
    width: 300px;
    margin-right: 10px;
}

.pagination {
    margin-top: 10px;
    display: flex;
    justify-content: center; 
}
</style>