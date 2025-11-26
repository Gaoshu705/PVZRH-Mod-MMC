<template>
<el-drawer
    :title="addFlag ? '添加' : '编辑'"
    :size="500"
    v-model="visibleFlag"
    :before-close="onClose"
>
  <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
    <el-form-item label="角色名称" prop="roleName" >
      <el-input v-model="form.roleName" placeholder="角色名称"/>
    </el-form-item>
    <el-form-item label="创建时间" prop="createTime" >
      <el-date-picker v-model="form.createTime" type="datetime"
                      placeholder="创建时间"
                      format="YYYY-MM-DD HH:mm:ss" value-format="YYYY-MM-DD HH:mm:ss"/>
    </el-form-item>
    <el-form-item label="更新时间" prop="updateTime" >
      <el-date-picker v-model="form.updateTime" type="datetime"
                      placeholder="更新时间"
                      format="YYYY-MM-DD HH:mm:ss" value-format="YYYY-MM-DD HH:mm:ss"/>
    </el-form-item>
    <el-form-item label="创建用户" prop="createUser" >
      <el-input v-model="form.createUser" placeholder="创建用户"/>
    </el-form-item>
    <el-form-item label="更新用户" prop="updateUser" >
      <el-input v-model="form.updateUser" placeholder="更新用户"/>
    </el-form-item>
  </el-form>

  <template #footer>
    <div class="drawer-footer">
      <el-button @click="onClose">取消</el-button>
      <el-button type="primary" @click="onSubmit">保存</el-button>
    </div>
  </template>
</el-drawer>
</template>
<script setup>
import {reactive, ref, nextTick} from 'vue';
import _ from 'lodash';
import {ElMessage} from 'element-plus';
import {roleApi} from '@/api/role-api';

// ------------------------ 联表查询VO ------------------------
const queryFormState = {
  pageNum: 1,
  pageSize: 10,
  sortItemList: []
}

// ------------------------ 枚举量 ------------------------

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
  addFlag.value = rowData.roleId == null;
  nextTick(() => {
    formRef.value.clearValidate();
  });
}

function onClose() {
  Object.assign(form, formDefault);
  visibleFlag.value = false;
}

// ------------------------ 表单 ------------------------

// 组件ref
const formRef = ref();

const formDefault = {
  roleId: undefined, // 角色ID
  roleName: undefined, // 角色名称
  createTime: undefined, // 创建时间
  updateTime: undefined, // 更新时间
  createUser: undefined, // 创建用户
  updateUser: undefined, // 更新用户
};

let form = reactive({...formDefault});

const rules = {
  roleName: [{
    required: true,
    message: '角色名称 必填',
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
      await roleApi.add(form);
    } else {
      await roleApi.update(form);
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
.pagination {
  display: flex;
  flex-direction: column;
  align-items: center;
}
</style>