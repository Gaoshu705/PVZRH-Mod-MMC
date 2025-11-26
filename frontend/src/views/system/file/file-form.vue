<template>
  <div class="drawer-form">
    <el-drawer
        :title="addFlag ? '添加' : '编辑'"
        :size="500"
        v-model="visibleFlag"
        :before-close="onClose"
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="文件夹类型" prop="folderType" >
          <el-select v-model="form.folderType" clearable placeholder="文件夹类型">
            <el-option v-for="(option, index) in folderTypeOptions" :key="index" :label="option.label" :value="option.value"/>
          </el-select>
        </el-form-item>
        <el-form-item label="文件名称" prop="fileName" >
          <el-input v-model="form.fileName" placeholder="文件名称"/>
        </el-form-item>
        <el-form-item label="文件大小" prop="fileSize" >
          <el-input-number v-model="form.fileSize" placeholder="文件大小"/>
        </el-form-item>
        <el-form-item label="文件类型" prop="fileType" >
          <el-input v-model="form.fileType" placeholder="文件类型"/>
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
  import {fileApi} from '@/api/file-api';
  // ------------------------ 枚举量 ------------------------
  const folderTypeOptions = [
    { label: '头像', value: 1 },
    { label: '其他', value: 2 },
  ]



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
    addFlag.value = rowData.fileId == null;
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
    fileId: undefined, // 文件ID
    folderType: undefined, // 文件夹类型
    fileName: undefined, // 文件名称
    fileSize: undefined, // 文件大小
    fileType: undefined, // 文件类型
    createTime: undefined, // 创建时间
    updateTime: undefined, // 更新时间
    createUser: undefined, // 创建用户
    updateUser: undefined, // 更新用户
  };

  let form = reactive({...formDefault});

  const rules = {
    folderType: [{
      required: true,
      message: '文件夹类型 必填',
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
        await fileApi.add(form);
      } else {
        await fileApi.update(form);
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