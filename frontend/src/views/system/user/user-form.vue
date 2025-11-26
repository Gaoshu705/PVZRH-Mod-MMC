<template>
  <div class="drawer-form">
    <el-drawer
        :title="addFlag ? '添加' : '编辑'"
        :size="500"
        v-model="visibleFlag"
        :before-close="onClose"
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" placeholder="用户名"/>
        </el-form-item>
        <el-form-item label="昵称" prop="nickname" >
          <el-input v-model="form.nickname" placeholder="昵称"/>
        </el-form-item>
        <el-form-item label="密码" prop="password" >
          <el-input v-model="form.password" placeholder="密码"/>
        </el-form-item>
        <el-form-item label="性别" prop="gender" >
          <el-select v-model="form.gender" clearable placeholder="性别">
            <el-option v-for="(option, index) in genderOptions" :key="index" :label="option.label" :value="option.value"/>
          </el-select>
        </el-form-item>
        <el-form-item label="头像" prop="avatar" >
          <el-upload
              action="#"
              :show-file-list="false"
              :before-upload="beforeAvatarUpload"
              :http-request="doAvatarUpload"
              class="mod-image-uploader"
          >
            <img v-if="form.avatar" :src="form.avatar" class="mod-img" />
            <el-icon v-else class="mod-image-uploader-icon">
              <Plus />
            </el-icon>
          </el-upload>
        </el-form-item>
        <el-form-item label="创建时间" prop="createTime"  v-if="false">
          <el-date-picker v-model="form.createTime" type="datetime"
                          placeholder="创建时间"
                          format="YYYY-MM-DD HH:mm:ss" value-format="YYYY-MM-DD HH:mm:ss"/>
        </el-form-item>
        <el-form-item label="更新时间" prop="updateTime"  v-if="false">
          <el-date-picker v-model="form.updateTime" type="datetime"
                          placeholder="更新时间"
                          format="YYYY-MM-DD HH:mm:ss" value-format="YYYY-MM-DD HH:mm:ss"/>
        </el-form-item>
        <el-form-item label="创建用户" prop="createUser"  v-if="false">
          <el-input v-model="form.createUser" placeholder="创建用户"/>
        </el-form-item>
        <el-form-item label="更新用户" prop="updateUser"  v-if="false">
          <el-input v-model="form.updateUser" placeholder="更新用户"/>
        </el-form-item>
        <el-form-item label="角色列表">
          <span>
            <el-button type="primary" plain @click="form.roleVOFlag = true"><el-icon><Plus /></el-icon>选择</el-button>
            <el-tag type="info" round style="margin-left: 5px" v-if="form.roleVOList">已选{{ form.roleVOList ? form.roleVOList.length : 0 }}</el-tag>
          </span>
          <el-dialog
              v-model="form.roleVOFlag"
              width="60%"
              align-center
              :z-index="999"
              :close-on-click-modal="false"
              :show-close="false"
          >
            <template #header>
              <span>角色列表</span>
              <span style="float: right;">
              <el-button type="primary" @click="saveRoleVO">保存</el-button>
              <el-button @click="form.roleVOFlag = false">关闭</el-button>
              </span>
            </template>
            <RoleVoList v-if="form.roleVOFlag"
                        :old-data="form.roleVOList" mode="edit"
                        ref="voRoleListRef"
            ></RoleVoList>
          </el-dialog>
        </el-form-item>
      </el-form>

      <template #footer>
        <div class="drawer-footer">
          <el-button @click="onClose">取消</el-button>
          <el-button type="primary" @click="onSubmit">保存</el-button>
        </div>
      </template>
    </el-drawer>
  </div>
</template>
<script setup>
  import {reactive, ref, nextTick} from 'vue';
  import _ from 'lodash';
  import {ElMessage} from 'element-plus';
  import {userApi} from '@/api/user-api';
  // ------------------------ 联表查询VO ------------------------
  const queryFormState = {
    pageNum: 1,
    pageSize: 10,
    sortItemList: []
  }

  // ------------------------ 中间表更新vo ------------------------
  import RoleVoList from "./role-vo-list.vue";
  const voRoleListRef = ref(null)
  function saveRoleVO() {
    if (voRoleListRef.value) {
      form.roleVOList = voRoleListRef.value.getSelectedRowList()
      ElMessage.success('保存成功')
      form.roleVOFlag = false
    }
  }
  // ------------------------ 枚举量 ------------------------
  const genderOptions = [
    { label: '男', value: 1 },
    { label: '女', value: 2 },
  ]



  // ------------------------ 图片上传 ------------------------
  import {fileApi} from "@/api/file-api.js";
  async function doAvatarUpload(options) {
    let file = options.file
    let fileForm = new FormData()
    fileForm.append('file', file)
    let res = await fileApi.upload(fileForm, 2)
    if (res.code === 0) { // on-success
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

  // ------------------------ 事件 ------------------------

  const emits = defineEmits(['reloadList']);

  // ------------------------ 显示与隐藏 ------------------------
  // 是否显示
  const visibleFlag = ref(false);
  // 是否新增
  const addFlag = ref(false);

  function show(rowData) {
    Object.assign(form, formDefault);
    if (rowData && !_.isEmpty(rowData)) {
      Object.assign(form, rowData);
    }
    visibleFlag.value = true;
    addFlag.value = rowData.userId == null;
    nextTick(() => {
      formRef.value.clearValidate();
    });
  }

  function onClose() {
    Object.keys(form).forEach(key => form[key] = null);
    visibleFlag.value = false;
  }

  // ------------------------ 表单 ------------------------

  // 组件ref
  const formRef = ref();

  const formDefault = {
    userId: undefined, // 用户ID
    username: undefined, // 用户名
    nickname: undefined, // 昵称
    password: undefined, // 密码
    gender: undefined, // 性别
    avatar: undefined, // 头像
    createTime: undefined, // 创建时间
    updateTime: undefined, // 更新时间
    createUser: undefined, // 创建用户
    updateUser: undefined, // 更新用户
  };

  let form = reactive({...formDefault});

  const rules = {
    username: [{
      required: true,
      message: '用户名 必填',
      trigger: 'blur'
    }],
    nickname: [{
      required: true,
      message: '昵称 必填',
      trigger: 'blur'
    }],
  };

  // 点击确定，验证表单
  async function onSubmit() {
    try {
      await formRef.value.validate();
      save();
    } catch (err) {
      ElMessage.error('参数验证错误，请仔细填写表单数据!');
    }
  }

  // 新建、编辑API
  async function save() {
    try {
      if (addFlag.value) {
        await userApi.add(form);
      } else {
        await userApi.update(form);
      }
      ElMessage.success('操作成功');
      emits('reloadList');
      onClose();
    } catch (err) {
      console.log(err)
    }
  }

  defineExpose({
    show,
  });
</script>
<style scoped lang="scss">
</style>