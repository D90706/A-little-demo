<template>
  <div class="container">
    <form class="login-form">
      <!-- 用户名 -->
      <div class="form-group">
        <label class="form-label" for="username">用户名:</label>
        <input type="text" id="username" name="username" placeholder="Username" autocomplete="current-username"
          v-model="data.user.username" @blur="validateField('username')" :class="{ 'invalid': errors.username }">
        <span class="error-message">{{ errors.username }}</span>
      </div>

      <!-- 密码 -->
      <div class="form-group">
        <label class="form-label" for="password">密码:</label>
        <input type="password" id="password" name="password" placeholder="Password" autocomplete="current-password"
          v-model="data.user.password" @blur="validateField('password')" :class="{ 'invalid': errors.password }">
        <span class="error-message">{{ errors.password }}</span>
      </div>

      <button type="button" class="submit-button" @click="login">登录</button>
      <div style="text-align:right;margin-top: 20px;">
        还没有账号？
        <a style="color: #007bff;" href="/registration">立即注册</a>
      </div>
    </form>


  </div>
</template>

<script setup>
import { reactive } from 'vue'
import request from '../utils/request'

// 错误信息存储
const errors = reactive({
  username: '',
  password: ''
})

const data = reactive({
  user: {
    username: '',
    password: ''
  },
  // 验证规则
  rules: {
    username: [
      { required: true, message: "用户名不能为空" }
    ],
    password: [
      { required: true, message: "密码不能为空" }
    ]
  }
})

// 验证单个字段
const validateField = (field) => {
  const value = data.user[field]
  const fieldRules = data.rules[field]
  let errorMsg = ''

  // 遍历该字段的所有验证规则
  for (const rule of fieldRules) {
    if (rule.required) {
      // 处理必填项验证
      if (!value || value.trim() === '') {
        errorMsg = rule.message
        break
      }
    }
  }

  errors[field] = errorMsg
  return !errorMsg  // 返回验证是否通过
}

// 验证所有字段
const validateAllFields = () => {
  let isAllValid = true
  // 遍历所有字段进行验证
  Object.keys(data.rules).forEach(field => {
    const isValid = validateField(field)
    if (!isValid) {
      isAllValid = false
    }
  })
  return isAllValid
}

const login = () => {
  // 先进行表单验证
  if (!validateAllFields()) {
    // 验证失败，滚动到第一个错误字段
    const firstErrorField = Object.keys(errors).find(field => errors[field])
    if (firstErrorField) {
      document.getElementById(firstErrorField).focus()
    }
    return  // 不提交请求
  }

  // 验证通过，发送登录请求
  request.post('/login', data.user).then(res => {
    console.log(res);
    if (res.data.code === "200") {
      localStorage.setItem("token", res.data.data);
      alert("登录成功");
      location.href = "/user";
    } else {
      alert("登录失败: " + (res.data.message || "用户名或密码错误"));
    }
  }).catch(err => {
    console.error('登录请求失败', err)
    alert('网络错误，请稍后重试')
  })
}
</script>

<style scoped>
.container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background-color: azure;
}

.login-form {
  background-color: white;
  padding: 2rem;
  border-radius: 0.5rem;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06);
  width: 100%;
  max-width: 28rem;
}

.form-group {
  margin-bottom: 1rem;
}

.form-label {
  display: block;
  margin-bottom: 0.5rem;
  font-weight: 500;
}

input[type="text"],
input[type="password"] {
  width: 100%;
  padding: 0.5rem 0.75rem;
  border: 1px solid #d1d5db;
  border-radius: 0.25rem;
  box-shadow: inset 0 1px 2px rgba(0, 0, 0, 0.05);
}

input[type="text"]:focus,
input[type="password"]:focus {
  outline: none;
  border-color: #6366f1;
  box-shadow: 0 0 0 2px rgba(99, 102, 241, 0.25);
}

/* 验证失败样式 */
input.invalid {
  border-color: #ef4444;
}

.error-message {
  color: #ef4444;
  font-size: 0.875rem;
  display: block;
  margin-top: 0.25rem;
  min-height: 1.2rem;
}

.submit-button {
  width: 100%;
  background-color: #6366f1;
  color: white;
  padding: 0.5rem 1rem;
  border-radius: 0.25rem;
  border: none;
  cursor: pointer;
  font-weight: 500;
}

.submit-button:hover {
  background-color: #4f46e5;
}
</style>