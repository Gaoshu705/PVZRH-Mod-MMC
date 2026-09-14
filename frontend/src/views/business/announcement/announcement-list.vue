<template>
<div class="announcement-list">
  <div class="query">
    <el-card>
      <div class="query-operation">
        <div class="sort-query">
          <div class="query-item">
            <div class="query-placeholder">排序字段:</div>
            <div class="query-input">
              <el-select
                  v-model="selectedSortItems"
                  multiple
                  collapse-tags
                  collapse-tags-tooltip
                  :max-collapse-tags="4"
                  placeholder="排序字段"
                  value-key="label"
                  @change="sortItemChange"
                  style="min-width: 260px; max-width: 600px;"
              >
                <template #tag>
                  <el-tag v-for="item in selectedSortItems"
                          :key="item.id"
                          :type="item.isAsc ? 'primary' : 'success'"
                          round closable
                          @close="handleTagClose(item)">
                    {{ item.label }}
                  </el-tag>
                </template>
                <el-option
                    v-for="item in sortItemOptions"
                    :key="item.label"
                    :label="item.label"
                    :value="item"
                    :disabled="item.disabled"
                >
                  <span style="float: left">{{ item.desc }}</span>
                  <span style="float: right;color: var(--el-text-color-secondary);font-size: 13px;">
                  <span v-if="item.isAsc">升序</span>
                  <span v-else>降序</span>
                </span>
                </el-option>
              </el-select>
            </div>
          </div>
        </div>
        <div class="search-query">
        </div>
        <div class="query-btn-group">
          <el-button type="primary" @click="onSearch">
            <el-icon>
              <search/>
            </el-icon>
            查询
          </el-button>
          <el-button @click="resetQuery">
            <el-icon>
              <refresh/>
            </el-icon>
            重置
          </el-button>
        </div>
      </div>
    </el-card>
  </div>

  <div class="list">
    <el-card>
      <div class="table-operation">
        <el-button @click="showForm" type="primary">
          <el-icon>
            <plus />
          </el-icon>
          新增
        </el-button>
        <el-button @click="confirmBatchDelete" type="danger" plain
                   :disabled="selectedRowKeyList.length === 0">
          <el-icon>
            <Delete />
          </el-icon>
          批量删除
        </el-button>
      </div>

      <div class="pagination-table">
        <el-table
            :data="tableData"
            :row-key="(row) => row.id"
            @selection-change="handleSelectionChange"
            border
            style="width: 100%"
        >
          <el-table-column type="selection" width="42" />
          <el-table-column prop="id" label="ID" min-width="80" align="center"/>
          <el-table-column prop="title" label="公告标题" min-width="120" align="center"/>
          <el-table-column prop="isPublished" label="是否发布" min-width="120" align="center">
            <template #default="scope">
              <el-tag :type="scope.row.isPublished ? 'success' : 'danger'">{{ scope.row.isPublished ? '是' : '否' }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="createdTime" label="创建时间" min-width="120" align="center"/>
          <el-table-column fixed="right" label="操作" width="120" align="center">
            <template #default="scope">
              <el-button link type="primary" @click="showForm(scope.row)">
                编辑
              </el-button>
              <el-button link type="danger" @click="onDelete(scope.row)">
                删除
              </el-button>
            </template>
          </el-table-column>
        </el-table>
        <div class="pagination">
          <el-pagination
              background
              :hide-on-single-page="false"
              layout="total, prev, pager, next, sizes"
              :total="total"
              v-model:page-size="queryForm.pageSize"
              v-model:current-page="queryForm.pageNum"
              @current-change="queryData"
              :page-sizes="[5, 10, 20, 50]"
              @size-change="handleSizeChange"
          />
        </div>
      </div>
    </el-card>
  </div>
</div>
<AnnouncementForm ref="formRef" @reloadList="queryData" />

</template>

<script setup>
import { ref, reactive, onMounted, watch } from 'vue'
import { announcementApi } from '@/api/announcement-api'
import AnnouncementForm from './announcement-form.vue'
import { Delete, Plus, Refresh, Search } from '@element-plus/icons-vue'
import {hasPerm} from "@/utils/permission.js";

// 查询数据表单和方法
const queryFormState = {
  pageNum: 1,
  pageSize: 10,
  sortItemList: []
}
const queryForm = reactive({ ...queryFormState })
const tableData = ref([])
const total = ref(0)

// 重置查询条件
function resetQuery() {
  selectedSortItems.value = []
  sortItemChange(selectedSortItems.value)
  let pageSize = queryForm.pageSize
  Object.assign(queryForm, queryFormState)
  queryForm.pageSize = pageSize
  queryData()
}

// 搜索
function onSearch() {
  queryForm.pageNum = 1
  queryData()
}

// 查询数据
async function queryData() {
  try {
    let queryResult = await announcementApi.page(queryForm)
    tableData.value = queryResult.data.list
    total.value = queryResult.data.total
  } catch (e) {
    console.log(e)
  }
}

function handleSizeChange(newSize) {
  queryForm.pageSize = newSize
  onSearch()
}


onMounted(queryData)

// 删除
const selectedRowKeyList = ref([])

function handleSelectionChange(val) {
  selectedRowKeyList.value = val.map(it => it.id)
}

async function onDelete(row) {
  if (row) {
    await handleDelete([row.id])
  } else {
    ElMessage.warning('请至少选择一条数据')
  }
}

async function handleDelete(id) {
  try {
    await ElMessageBox.confirm('确定要删除吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await announcementApi.delete(id)
    ElMessage.success('删除成功')
    queryData()
  } catch (e) {
  }
}

// 批量删除
function confirmBatchDelete() {
  ElMessageBox.confirm(
      '确定要批量删除这些数据吗?',
      '提示',
      {
        confirmButtonText: '删除',
        cancelButtonText: '取消',
        type: 'warning'
      }
  )
      .then(() => {
        requestBatchDelete()
      })
      .catch(() => {
      })
}

// 请求批量删除
async function requestBatchDelete() {
  try {
    await announcementApi.batchDelete(selectedRowKeyList.value)
    ElMessage.success('删除成功')
    queryData()
  } catch (e) {
    ElMessage.error('删除失败')
  }
}

// 表单相关
const formRef = ref(null)

function showForm(row) {
  formRef.value.show(row)
}

// 排序字段
const sortItemOptions = ref([
  { label: 'ID降序', desc: 'ID', isAsc: false, column: 'id', disabled: false },
  { label: 'ID升序', desc: 'ID', isAsc: true, column: 'id', disabled: false },
  { label: '公告标题降序', desc: '公告标题', isAsc: false, column: 'title', disabled: false },
  { label: '公告标题升序', desc: '公告标题', isAsc: true, column: 'title', disabled: false },
  { label: '是否发布降序', desc: '是否发布', isAsc: false, column: 'isPublished', disabled: false },
  { label: '是否发布升序', desc: '是否发布', isAsc: true, column: 'isPublished', disabled: false },
  { label: '创建时间降序', desc: '创建时间', isAsc: false, column: 'createdTime', disabled: false },
  { label: '创建时间升序', desc: '创建时间', isAsc: true, column: 'createdTime', disabled: false },
  { label: '更新时间降序', desc: '更新时间', isAsc: false, column: 'updatedTime', disabled: false },
  { label: '更新时间升序', desc: '更新时间', isAsc: true, column: 'updatedTime', disabled: false },
])

const selectedSortItems = ref([])

function sortItemChange(selected) {
  sortItemOptions.value.forEach(option => {
    option.disabled = false
  })
  const selectedColumns = new Map()
  selected.forEach(item => {
    selectedColumns.set(item.column, item.isAsc)
  })
  sortItemOptions.value.forEach(option => {
    if (selectedColumns.has(option.column)) {
      option.disabled = selectedColumns.get(option.column) !== option.isAsc
    }
  })
}

function handleTagClose(tag) {
  selectedSortItems.value.splice(selectedSortItems.value.indexOf(tag), 1)
  sortItemChange(selectedSortItems.value)
}

watch(selectedSortItems, (newSelectedSortItems, oldSelectedSortItems) => {
  queryForm.sortItemList = newSelectedSortItems.map(item => {
    return { isAsc: item.isAsc, column: item.column }
  })
}, { deep: true })
</script>
<style scoped lang="scss">
.announcement-list{
  .query {
    .el-card__body {
      padding: 10px 10px 10px 10px;
    }

    .query-operation {
      display: flex;
      align-items: center;
      flex-wrap: wrap;


      .query-item {
        display: flex;
        align-items: center;

        .query-placeholder {
          margin-right: 10px;
          padding: 5px 0;
        }

        .query-input {
          margin-right: 12px;
          padding: 5px 0;
        }
      }

      .sort-query {
        display: flex;
        align-items: center;
        flex-wrap: wrap;
      }

      .search-query {
        display: flex;
        align-items: center;
        flex-wrap: wrap;
      }

      .query-btn-group {
        padding: 5px 0;
      }
    }
  }

  .list {
    margin-top: 10px;

    .table-operation {
      margin-bottom: 10px;
    }

    .pagination {
      margin-top: 10px;
      float: right;
    }
  }
}
</style>
