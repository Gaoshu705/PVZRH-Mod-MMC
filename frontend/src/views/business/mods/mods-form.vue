<template>
  <div class="drawer-form">
    <el-drawer :title="addFlag ? '添加' : '编辑'" :size="500" v-model="visibleFlag" :before-close="onClose">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="Mod名称" prop="modName">
          <el-input v-model="form.modName" placeholder="Mod名称" />
        </el-form-item>
        <el-form-item label="Mod英文名" prop="englishName">
          <el-input v-model="form.englishName" placeholder="Mod英文名" />
        </el-form-item>
        <el-form-item label="作者ID" prop="authorId" v-if="hasPerm('business:mod:sup')">
          <!-- <el-input v-model="form.authorId" placeholder="作者ID" /> -->
          <el-select v-model="form.authorId" placeholder="请选择作者" style="width: 240px">
            <el-option v-for="item in userList" :key="item.userId" :label="item.nickname" :value="item.userId">
              <div class="option-item">
                <el-avatar size="small" :src="item.avatar" style="margin-right: 8px;" />
                <span :style="{ color: item.userId }">{{ item.nickname }}</span>
              </div>
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="Mod介绍" prop="modDescription">
          <el-input v-model="form.modDescription" placeholder="Mod介绍" />
        </el-form-item>
        <el-form-item label="视频Url" prop="videoUrl">
          <el-input v-model="form.videoUrl" placeholder="视频Url" />
        </el-form-item>
        <el-form-item label="支持游戏" prop="gameName">
          <el-input v-model="form.gameName" placeholder="支持游戏" />
        </el-form-item>
        <el-form-item label="支持版本" prop="supportedVersions">
          <el-input v-model="form.supportedVersions" placeholder="支持版本" />
        </el-form-item>
        <el-form-item label="Mod框架" prop="frameworkName">
          <el-select v-model="form.frameworkName" clearable placeholder="Mod框架">
            <el-option v-for="(option, index) in frameworkNameOptions" :key="index" :label="option.label"
              :value="option.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="直链下载地址" prop="downloadDirectUrl" label-width="auto">
          <el-input v-model="form.downloadDirectUrl" placeholder="直链下载地址" />
        </el-form-item>
        <el-form-item label="网盘下载地址" prop="downloadCloudUrl" label-width="auto">
          <el-input v-model="form.downloadCloudUrl" placeholder="网盘下载地址" />
        </el-form-item>
        <el-form-item label="Mod版本" prop="version">
          <el-input v-model="form.version" placeholder="Mod版本" />
        </el-form-item>
        <el-form-item label="文件大小" prop="fileSize" v-if="false">
          <el-input-number v-model="form.fileSize" placeholder="文件大小" />
        </el-form-item>
        <el-form-item label="下载次数" prop="downloadCount" v-if="false">
          <el-input v-model="form.downloadCount" placeholder="下载次数" />
        </el-form-item>
        <el-form-item label="查看次数" prop="viewCount" v-if="false">
          <el-input v-model="form.viewCount" placeholder="查看次数" />
        </el-form-item>
        <el-form-item label="是否通过审核" prop="isApproved" v-if="false">
          <el-switch v-model="form.isApproved" :active-value="true" />
        </el-form-item>
        <el-form-item label="是否推荐" prop="isFeatured" v-if="hasPerm('business:mod:sup')">
          <el-switch v-model="form.isFeatured" :active-value="true" />
        </el-form-item>
        <el-form-item label="添加共创" prop="otherAuthor">
          <el-select v-model="form.otherAuthors" multiple placeholder="请选择作者" style="width: 240px">
            <el-option v-for="item in userList" :key="item.userId" :label="item.nickname" :value="item.userId">
              <div class="option-item">
                <el-avatar size="small" :src="item.avatar" style="margin-right: 8px;" />
                <span :style="{ color: item.userId }">{{ item.nickname }}</span>
              </div>
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="是否显示直链" prop="showDirectUrl">
          <el-switch v-model="form.showDirectUrl" :active-value="true" />
        </el-form-item>
        <el-form-item label="是否发布" prop="isVisible">
          <el-switch v-model="form.isVisible" :active-value="true" />
        </el-form-item>
        <el-form-item label="创建时间" prop="createdAt" v-if="false">
          <el-date-picker v-model="form.createdAt" type="datetime" placeholder="创建时间" format="YYYY-MM-DD HH:mm:ss"
            value-format="YYYY-MM-DD HH:mm:ss" />
        </el-form-item>
        <el-form-item label="修改时间" prop="updatedAt" v-if="false">
          <el-date-picker v-model="form.updatedAt" type="datetime" placeholder="修改时间" format="YYYY-MM-DD HH:mm:ss"
            value-format="YYYY-MM-DD HH:mm:ss" />
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
import { reactive, ref, nextTick } from 'vue';
import _ from 'lodash';
import { ElMessage } from 'element-plus';
import { modsApi } from '@/api/mods-api';
import { userApi } from '@/api/user-api';
import { hasPerm } from "@/utils/permission.js";
// ------------------------ 联表查询VO ------------------------
const queryFormState = {
  pageNum: 1,
  pageSize: 10,
  sortItemList: []
}

// ------------------------ 中间表更新vo ------------------------
// ------------------------ 枚举量 ------------------------
const frameworkNameOptions = [
  { label: 'Bepinex', value: '1' },
  { label: 'MelonLoader', value: '2' },
]



// ------------------------ 图片上传 ------------------------
// ------------------------ 事件 ------------------------

const emits = defineEmits(['reloadList']);

// ------------------------ 显示与隐藏 ------------------------
// 是否显示
const visibleFlag = ref(false);
// 是否新增
const addFlag = ref(false);

const userList = ref([]);

function show(rowData) {
  userApi.getList().then(res => {
    console.log(res);
    userList.value = res.data;
  });
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

function onSelectUser(item) {
  form.otherAuthor = item.nickname;
}

// ------------------------ 表单 ------------------------

// 组件ref
const formRef = ref();

const formDefault = {
  id: undefined, // ID
  modName: undefined, // Mod名称
  englishName: undefined, // Mod英文名
  authorId: undefined, // 作者ID
  modDescription: undefined, // Mod介绍
  gameName: undefined, // 支持游戏
  supportedVersions: undefined, // 支持版本
  frameworkName: undefined, // Mod框架
  downloadDirectUrl: undefined, // 直链下载地址
  downloadCloudUrl: undefined, // 网盘下载地址
  version: undefined, // Mod版本
  fileSize: undefined, // 文件大小
  otherAuthors: undefined, // 其它作者
  showDirectUrl: undefined, // 是否显示直链
  downloadCount: undefined, // 下载次数
  viewCount: undefined, // 查看次数
  isApproved: undefined, // 是否通过审核
  isFeatured: undefined, // 是否推荐
  isVisible: undefined, // 是否可见
  createdAt: undefined, // 创建时间
  updatedAt: undefined, // 修改时间
};

let form = reactive({ ...formDefault });

const rules = {
  modName: [{
    required: true,
    message: 'Mod名称 必填',
    trigger: 'blur'
  }],
  downloadDirectUrl: [{
    required: true,
    message: '直链下载地址 必填',
    trigger: 'blur'
  }, {
    pattern: /^https?:\/\/[^\s]+$/,
    message: '直链下载地址格式不正确',
    trigger: 'blur'
  }],
  downloadCloudUrl: [{
    required: true,
    message: '网盘下载地址 必填',
    trigger: 'blur'
  }, {
    pattern: /^https?:\/\/[^\s]+$/,
    message: '网盘下载地址格式不正确',
    trigger: 'blur'
  }],
  version: [{
    message: 'Mod版本 必填',
    trigger: 'blur'
  }, {
    pattern: /^\d+\.\d+\.\d+(-[a-zA-Z]+)?$/,
    message: '版本格式不正确，应为 X.Y.Z 或 X.Y.Z-类型',
    trigger: 'blur'
  }]
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
      await modsApi.add(form);
    } else {
      await modsApi.update(form);
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
.option-item {
  display: flex;
  align-items: center;
}
</style>