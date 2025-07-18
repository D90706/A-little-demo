<template>
   <div>
      <el-input v-model="data.input" style="width: 300px" :prefix-icon="Search" :suffix-icon="Calendar"
         placeholder="请输入内容" clearable></el-input>
      <h1>{{ data.input }}</h1>
   </div>

   <div>
      <el-select style="width:300px" v-model="value" multiple>
         <el-option v-for="item in fruits" :key="item" :label="item" :value="item">{{ item }}</el-option>
      </el-select>
      <h1>{{ value }}</h1>
   </div>

   <div>
      <el-radio-group v-model="sex">
         <el-radio value="男">男</el-radio>
         <el-radio value="女">女</el-radio>
         <el-radio-button value="保密">保密</el-radio-button>
      </el-radio-group>
      <h1>{{ sex }}</h1>
   </div>


   <div style="margin: 20px 0">
      <el-checkbox-group v-model="checkList">
         <el-checkbox v-for="item in options" :key="item.id" :value="item.name" :label="item.label" />
      </el-checkbox-group>
      <span style="margin-left: 20px">{{ checkList }}</span>
   </div>

   <div style="margin: 20px 0">
      <el-table :data="tabledata" style="width: 100%" stripe>
         <el-table-column prop="id" label="id" width="180" />
         <el-table-column prop="name" label="name" width="180" />
         <el-table-column prop="age" label="age" />
      </el-table>
      <el-pagination

          v-model:current-page="data.currentPage"
          v-model:page-size="data.pageSize"
          :page-sizes="[5, 10, 15, 20]"
          设置背景颜色
          background
          layout="total, sizes, prev, pager, next, jumper"
          :total="47"
        />

   </div>
</template>

<script setup lang="js">
import { Calendar, Search } from '@Element-plus/icons-vue';
import { elPaginationKey } from 'element-plus';
import { reactive, ref } from 'vue';
import img1 from "@/assets/logo.svg"
import request from '@/utils/request';


const data = reactive({
   input: 'Hello Vue3',
   employeelist: [],
})

const fruits = ['apple', 'banana', 'orange']
const value = ref("")
const sex = ref("男")
const options = ref([{ id: 1, name: "苹果", label: "苹果" },
{ id: 2, name: "香蕉", label: "香蕉" },
{ id: 3, name: "橘子", label: "橘子" },
{ id: 4, name: "西瓜", label: "西瓜" }])
const checkList = ref([])

const tabledata = [{ id: 1, name: "zhangsan", age: 18 },
{ id: 2, name: "lisi", age: 20 },
{ id: 3, name: "wangwu", age: 22 },
{ id: 2, name: "lisi", age: 20 },
]

request.get("/employee/selectAll").then(res=>{
   
   data.employeelist = res.data;
   console.log(data.employeelist.data)
})

</script>

<style scoped></style>
