<template>
<el-drawer
    :title="addFlag ? '添加' : '编辑'"
    :size="500"
    v-model="visibleFlag"
    :before-close="onClose"
>
  <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
    <el-form-item label="公告标题" prop="title" >
      <el-input v-model="form.title" placeholder="公告标题"/>
    </el-form-item>
    <el-form-item label="公告内容" prop="content" >
      <el-input type="textarea" :rows="6" v-model="form.content"
                placeholder="公告内容"/>
    </el-form-item>
    <el-form-item label="是否发布" prop="isPublished" >
      <el-switch v-model="form.isPublished" :active-value="true"/>
    </el-form-item>
    <el-form-item label="创建时间" prop="createdTime"  v-if="false">
      <el-date-picker v-model="form.createdTime" type="datetime"
                      placeholder="创建时间"
                      format="YYYY-MM-DD HH:mm:ss" value-format="YYYY-MM-DD HH:mm:ss"/>
    </el-form-item>
    <el-form-item label="更新时间" prop="updatedTime"  v-if="false">
      <el-date-picker v-model="form.updatedTime" type="datetime"
                      placeholder="更新时间"
                      format="YYYY-MM-DD HH:mm:ss" value-format="YYYY-MM-DD HH:mm:ss"/>
    </el-form-item>
  </el-form>

  <div class="drawer-footer">
    <el-button @click="onClose">取消</el-button>
    <el-button type="primary" @click="onSubmit">保存</el-button>
  </div>
</el-drawer>
</template>
<script setup>
import {reactive, ref, nextTick} from 'vue';
import _ from 'lodash';
import {ElMessage} from 'element-plus';
import {announcementApi} from '@/api/announcement-api';

const emits = defineEmits(['reloadList']);

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
  addFlag.value = rowData.id == null;
  nextTick(() => {
    formRef.value.clearValidate();
  });
}

function onClose() {
  Object.keys(form).forEach(key => form[key] = null);
  visibleFlag.value = false;
}

// 组件ref
const formRef = ref();

const formDefault = {
  id: undefined,
  title: undefined,
  content: undefined,
  isPublished: undefined,
  createdTime: undefined,
  updatedTime: undefined,
};

let form = reactive({...formDefault});

const rules = {
  title: [{
    required: true,
    message: '公告标题 必填',
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
      await announcementApi.add(form);
    } else {
      await announcementApi.update(form);
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
