<template>
    <!-- 搜索区域 -->
    <el-card class="card">
        <el-input class="input" v-model="data.name" placeholder="请输入数据" prefix-icon="Search"></el-input>
        <el-button type="primary" @click="load">搜索</el-button>
        <el-button type="warning" @click="reset">重置</el-button>
    </el-card>
    <!-- 操作区域 -->
    <el-card class="card">
        <el-button type="primary" @click="handAdd">添加</el-button>
        <el-button type="danger" @click="delBatch">批量删除</el-button>
        <el-button type="info" @click="">导入</el-button>
        <el-button type="success" @click="">导出</el-button>
    </el-card>
    <!-- 数据展示区域 -->
    <el-card>
        <div>
            <el-table :data="data.tabledata" stripe @selection-change="handleSelectionChange">
                <el-table-column type="selection" />
                <el-table-column label="ID" prop="id" />
                <el-table-column label="姓名" prop="name" />
                <el-table-column label="性别" prop="sex" />
                <el-table-column label="年龄" prop="age" />
                <el-table-column label="个人介绍" prop="description" show-overflow-tooltip />
                <el-table-column label="任职部门" prop="deptName" />
                <el-table-column label="操作" width="180">
                    <template #default="scope">
                        <el-button type="primary" circle :icon="Edit" @click="handleUpdate(scope.row)"></el-button>
                        <el-button type="danger" circle :icon="Delete" @click="del(scope.row.id)"></el-button>
                    </template>
                </el-table-column>
            </el-table>
            <!-- 分页器 -->
            <div class="pagination">
                <el-pagination @size-change="load" @current-change="load" v-model:current-page="data.pageNum"
                    v-model:page-size="data.pageSize" :page-sizes="[5, 10, 15, 20]" background
                    layout="total, sizes, prev, pager, next, jumper" :total="data.total" />
            </div>

            <!-- 添加功能 -->
            <!-- el-dialog弹窗标签，里面放el-from表单 -->
            <el-dialog v-model="data.formVisible" title="员工信息" width="500">
                <el-form :model="data.form" label-width="80px" padding-top="20px" padding-right="30px">
                    <el-form-item label="姓名">
                        <el-input v-model="data.form.name" autocomplete="off" placeholder="请输入姓名" />
                    </el-form-item>
                    <el-form-item label="性别" style="margin-right:30px">
                        <el-radio-group v-model="data.form.sex">
                            <el-radio value="男" label="男"></el-radio>
                            <el-radio value="女" label="女"></el-radio>
                        </el-radio-group>
                    </el-form-item>
                    <el-form-item label="年龄">
                        <el-input-number size="large" :max="150" :min="18" v-model="data.form.age" autocomplete="off"
                            placeholder="请输入年龄" />
                    </el-form-item>
                    <el-form-item label="个人介绍">
                        <el-input :rows=3 type="textarea" v-model="data.form.description" autocomplete="off"
                            placeholder="请输入个人介绍" />
                    </el-form-item>
                    <el-form-item label="任职部门">
                    </el-form-item>
                </el-form>
                <template #footer>
                    <div class="dialog-footer">
                        <el-button @click="data.formVisible = false">取消</el-button>
                        <el-button type="primary" @click="save">保存</el-button>
                    </div>
                </template>
            </el-dialog>


        </div>
    </el-card>

</template>
<script setup>
import { reactive } from 'vue';
import request from '@/utils/request';
import { Search, Edit, Delete } from '@Element-plus/icons-vue';
import { ElMessage, ElMessageBox } from 'element-plus';

// 全局变量
const data = reactive({
    name: "",
    pageNum: 1,
    pageSize: 10,
    tabledata: [],
    total: 0,
    formVisible: false,
    form: {
        name: "",
        sex: "",
        age: "",
        description: "",
        deptId: ""
    },
    ids: []
})
//加载数据
const load = () => {
    request.get("/employee/selectPage", {//?pageNum=1&pageSize=10
        params: {
            pageNum: data.pageNum,
            pageSize: data.pageSize,
            name: data.name
        }
    }).then(res => {
        data.tabledata = res.data.data.list;
        data.total = res.data.data.total;
        console.log();
    })
}

load();
//重置
const reset = () => {
    data.name = "";
    load();
}

//添加功能
function handAdd() {
    data.formVisible = true;
    data.form = {}//清空上次留下来的数据，避免残留

}
//保存功能，判断新增还是更新
const save = () => {
    data.form.id ? update() : add();
}

//添加功能
const add = () => {
    request.post("employee/add", data.form).then(res => {
        if (res.data.code == "200") {
            ElMessage.success("添加成功");
            load();
            data.formVisible = false;
        } else {
            ElMessage.error("添加失败");
        }
    })
}


//处理更新功能
const handleUpdate = (row) => {
    data.form = JSON.parse(JSON.stringify(row));//深拷贝，避免修改原数据
    data.formVisible = true;
}
const update = () => {
    request.put("employee/update", data.form).then(res => {
        if (res.data.code == "200") {
            ElMessage.success("更新成功");
            load();
            data.formVisible = false;
        } else {
            ElMessage.error("更新失败");
        }
    })
}
//删除功能
const del = (id) => {
    ElMessageBox.confirm("删除数据后无法恢复，确认删除？", "删除确认", { type: "warning" }).then(() => {
        request.delete("employee/delete/" + id).then(res => {
            if (res.data.code == "200") {
                ElMessage.success("删除成功");
                load();
            } else {
                ElMessage.error("删除失败");
            }
        })
    })
}
//批量选中，获取id功能
const handleSelectionChange = (rows) => {
    data.ids = rows.map(row => row.id);
    console.log(data.ids);
}

//批量删除功能
const delBatch = () => {
    //判断是否选择数据
    if (data.ids.length === 0) {
        ElMessage.warning({
            options: '请选择数据'
        })
        return
    }
    //二次确认
    ElMessageBox.confirm('删除数据后无法恢复，您确认删除吗？', '删除确认', {
        type: 'warning'
    }).then(() => {
        request.delete('/employee/deleteBatch', { data: data.ids }).then(res => {
            if (res.data.code =="200") {
                ElMessage.success('批量删除成功成功')
                load() // 删除后一定要重新加载最新的数据
            } else {
                ElMessage.error(res.msg)
            }
        })
    }).catch()
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