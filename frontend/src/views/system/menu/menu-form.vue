<template>
  <div class="drawer-form">
    <el-drawer
        :title="addFlag ? '添加' : '编辑'"
        :size="500"
        v-model="visibleFlag"
        :before-close="onClose"
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="菜单名称" prop="menuName" >
          <el-input v-model="form.menuName" placeholder="菜单名称"/>
        </el-form-item>
        <el-form-item label="父菜单ID" prop="parentId" >
          <el-input v-model="form.parentId" placeholder="父菜单ID"/>
        </el-form-item>
        <el-form-item label="权限类型" prop="type" >
          <el-select v-model="form.type" clearable placeholder="权限类型">
            <el-option v-for="(option, index) in typeOptions" :key="index" :label="option.label" :value="option.value"/>
          </el-select>
        </el-form-item>
        <el-form-item label="权限字符串" prop="code" >
          <el-input v-model="form.code" placeholder="权限字符串"/>
        </el-form-item>
        <el-form-item label="路由地址" prop="uri" >
          <el-input v-model="form.uri" placeholder="路由地址"/>
        </el-form-item>
        <el-form-item label="组件路径" prop="componentPath" >
          <el-input v-model="form.componentPath" placeholder="组件路径"/>
        </el-form-item>
        <el-form-item label="图标" prop="icon" >
          <IconPicker v-model="form.icon"></IconPicker>
        </el-form-item>
        <el-form-item label="排序优先级" prop="priority" >
          <el-input-number v-model="form.priority" placeholder="排序优先级"/>
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
  import {menuApi} from '@/api/menu-api';
  import IconPicker from "@/components/icon-picker.vue";
  // ------------------------ 枚举量 ------------------------
  const typeOptions = [
    { label: '目录', value: 1 },
    { label: '菜单', value: 2 },
    { label: '功能点', value: 3 },
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
    addFlag.value = rowData.menuId == null;
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
    menuId: undefined, // 菜单ID
    menuName: undefined, // 菜单名称
    parentId: undefined, // 父菜单ID
    type: undefined, // 权限类型
    code: undefined, // 权限字符串
    uri: undefined, // 路由地址
    componentPath: undefined, // 组件路径
    icon: undefined, // 图标
    priority: undefined, // 排序优先级
    createTime: undefined, // 创建时间
    updateTime: undefined, // 更新时间
    createUser: undefined, // 创建用户
    updateUser: undefined, // 更新用户
  };

  let form = reactive({...formDefault});

  const rules = {
    menuName: [{
      required: true,
      message: '菜单名称 必填',
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
        await menuApi.add(form);
      } else {
        await menuApi.update(form);
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