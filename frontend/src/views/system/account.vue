<template>
  <el-card style="width: 100%;height: 100%">
    <div class="account-content" style="width: 60%">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="用户ID" prop="userId">
          <el-input v-model="form.userId" placeholder="用户ID" disabled/>
        </el-form-item>
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" placeholder="用户名" disabled/>
        </el-form-item>
        <el-form-item label="昵称" prop="nickname">
          <el-input v-model="form.nickname" placeholder="昵称"/>
        </el-form-item>
        <el-form-item label="新密码" prop="password">
          <el-input v-model="form.newPassword" placeholder="新密码"/>
        </el-form-item>
        <el-form-item label="性别" prop="gender">
          <el-select v-model="form.gender" clearable placeholder="性别">
            <el-option v-for="item in genderOptions" :key="item.value" :label="item.label" :value="item.value"/>
          </el-select>
        </el-form-item>
        <el-form-item label="头像" prop="avatar">
          <el-upload
              action="#"
              :show-file-list="false"
              :before-upload="beforeAvatarUpload"
              :http-request="doAvatarUpload"
              class="mod-image-uploader"
          >
            <img v-if="form.avatar" :src="form.avatar" class="mod-img"/>
            <el-icon v-else class="mod-image-uploader-icon">
              <Plus/>
            </el-icon>
          </el-upload>
        </el-form-item>
      </el-form>

      <div>
        <el-button type="primary" @click="onSubmit">更新</el-button>
      </div>
    </div>

  </el-card>
</template>

<script setup>

import {reactive, ref, nextTick} from 'vue'
import _ from 'lodash'
import {ElMessage} from 'element-plus'
import {userApi} from '@/api/user-api'
import {useUserStore} from '@/stores/user.js'
import {fileApi} from "@/api/file-api.js";

const genderOptions = [
  {label: '未知', value: 0},
  {label: '男', value: 1},
  {label: '女', value: 2}
]

async function doAvatarUpload(options) {
  let file = options.file
  let fileForm = new FormData()
  fileForm.append('file', file)
  let res = await fileApi.upload(fileForm, 1)
  if (res.code === 0) { // on-success
    console.log(res.data)
  form.avatar = res.data.fileUrl
  } else {
    ElMessage.error(res.msg)
  }
}

function beforeAvatarUpload(rawFile) {
  if (rawFile.type !== 'image/jpeg' &&
      rawFile.type !== 'image/png') {
    ElMessage.error('文件格式错误: 不支持' + rawFile.type)
    return false
  } else if (rawFile.size / 1024 / 1024 > 10) {
    ElMessage.error('文件大小不能超过10MB!')
    return false
  }
  return true
}


// ------------------------ 表单 ------------------------

// 组件ref
const formRef = ref()

const formDefault = {
  userId: useUserStore().userId, //用户ID
  username: useUserStore().username, //用户名
  nickname: useUserStore().nickname, //昵称
  password: undefined, //密码
  newPassword: undefined, //新密码
  gender: useUserStore().gender, //性别，0未知，1男，2女
  avatar: useUserStore().avatar, //头像URL
}

let form = reactive({...formDefault})

const rules = {
  username: [{
    required: true,
    message: '用户名 必填',
    trigger: 'blur'
  }],
  phone: [{
    required: true,
    message: '手机号码 必填',
    trigger: 'blur'
  }],
  gender: [{
    required: true,
    message: '性别，0未知，1男，2女 必填',
    trigger: 'blur'
  }],
  avatar: [{
    required: true,
    message: '头像 必填',
    trigger: 'blur'
  }],
}

// 点击确定，验证表单
async function onSubmit() {
  try {
    await formRef.value.validate()
    save()
  } catch (err) {
    ElMessage.error('参数验证错误，请仔细填写表单数据!')
  }
}

// 新建、编辑API
async function save() {
  try {
    await userApi.update(form)
    ElMessage.success('操作成功')
  } catch (err) {
    console.log(err)
  }
}


</script>
<style lang="scss" scoped>
.account-content {
  align-items: center;
  flex-direction: column;
  justify-items: center;
}


.mod-image-uploader .el-upload {
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: var(--el-transition-duration-fast);
}

.mod-image-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 178px;
  height: 178px;
  line-height: 178px;
  text-align: center;
  border: 1px dashed var(--el-border-color);
}

.mod-image-uploader-icon:hover {
  border-color: var(--el-color-primary);
}

.mod-img {
  width: 178px;
  height: 178px;
  display: block;
}
</style>
