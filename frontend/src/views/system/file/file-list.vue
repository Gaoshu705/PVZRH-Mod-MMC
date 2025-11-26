<template>
  <div class="table-list" v-if="hasPerm('system:file:get')">
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
                    value-key="column"
                    @change="sortItemChange"
                    style="min-width: 260px; max-width: 600px;"
                >
                  <template #tag>
                    <el-tag
                        v-for="item in selectedSortItems"
                        :key="item.column"
                        :type="item.isAsc ? 'success' : 'warning'"
                        class="sort-tag"
                        closable
                        @close="handleTagClose(item)"
                    >
                      {{ item.label }}
                      <div class="sort-direction-wrapper" @click.stop="toggleSortDirection(item)">
                        <el-icon class="sort-direction" :class="{ 'is-asc': item.isAsc }">
                          <ArrowUp v-if="item.isAsc"/>
                          <ArrowDown v-else/>
                        </el-icon>
                      </div>
                    </el-tag>
                  </template>
                  <el-option
                      v-for="item in sortItemOptions"
                      :key="item.column"
                      :label="item.label"
                      :value="item"
                      :disabled="isColumnSelected(item.column)"
                  >
                    {{ item.label }}
                  </el-option>
                </el-select>
              </div>
            </div>
          </div>
          <div class="search-query">
            <div class="query-item">
              <div class="query-placeholder">文件名称:</div>
              <div class="query-input">
                <el-input v-model="queryForm.fileName" placeholder="文件名称"/>
              </div>
            </div>
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
          <el-button @click="showForm" type="primary" v-if="hasPerm('system:file:add')">
            <el-icon>
              <plus />
            </el-icon>
            新增
          </el-button>
          <el-button @click="confirmBatchDelete" type="danger" plain
                     :disabled="selectedRowKeyList.length === 0" v-if="hasPerm('system:file:del')">
            <el-icon>
              <Delete />
            </el-icon>
            批量删除
          </el-button>
        </div>

        <div class="pagination-table">
          <el-table
              :data="tableData"
              :row-key="(row) => row.fileId"
              @selection-change="handleSelectionChange"
              border
              style="width: 100%"
          >
            <el-table-column type="selection" width="42" />
            <el-table-column prop="fileId" label="文件ID" min-width="120" />
            <el-table-column prop="folderType" label="文件夹类型" min-width="120">
              <template #default="scope">
                {{ getFolderTypeOptionsLabel(scope.row.folderType) }}
              </template>
            </el-table-column>
            <el-table-column prop="fileName" label="文件名称" min-width="120" />
            <el-table-column prop="fileSize" label="文件大小" min-width="120" />
            <el-table-column prop="fileType" label="文件类型" min-width="120" />
            <el-table-column prop="createTime" label="创建时间" min-width="120" />
            <el-table-column prop="updateTime" label="更新时间" min-width="120" />
            <el-table-column prop="createUser" label="创建用户" min-width="120" />
            <el-table-column prop="updateUser" label="更新用户" min-width="120" />
            <el-table-column fixed="right" label="操作" width="120">
              <template #default="scope">
                <el-button link type="primary" @click="showForm(scope.row)" v-if="hasPerm('system:file:upd')">
                  编辑
                </el-button>
                <el-button link type="danger" @click="onDelete(scope.row)" v-if="hasPerm('system:file:del')">
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
  <FileForm ref="formRef" @reloadList="queryData" />

</template>

<script setup>
  import { ref, reactive, onMounted, watch } from 'vue'
  import { fileApi } from '@/api/file-api'
  import FileForm from './file-form.vue'
  import {Delete, Plus, Refresh, Search, ArrowUp, ArrowDown} from '@element-plus/icons-vue'
  import {hasPerm} from "@/utils/permission.js";
  // ------------------------ 枚举量 ------------------------
  const folderTypeOptions = [
    { label: '头像', value: 1 },
    { label: '其他', value: 2 },
  ]
  function getFolderTypeOptionsLabel(value) {
    const option = folderTypeOptions.find(option => option.value === value)
    return option ? option.label : ''
  }


  // --------------------------------------------------------

  // 查询数据表单和方法
  const queryFormState = {
    pageNum: 1,
    pageSize: 10,
    fileName: null,
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
      let queryResult = await fileApi.page(queryForm)
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
    selectedRowKeyList.value = val.map(it => it.fileId)
  }

  async function onDelete(row) {
    if (row) {
      await handleDelete([row.fileId])
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
      await fileApi.delete(id)
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
          // 当用户点击取消按钮时，这里可以处理一些逻辑
        })
  }

  // 请求批量删除
  async function requestBatchDelete() {
    try {
      await fileApi.batchDelete(selectedRowKeyList.value)
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
  // -------------------------- 排序字段 -------------------------
  const sortItemOptions = ref([
    {label: '文件ID', column: 'file_id', isAsc: false},
    {label: '文件夹类型', column: 'folder_type', isAsc: false},
    {label: '文件名称', column: 'file_name', isAsc: false},
    {label: '文件大小', column: 'file_size', isAsc: false},
    {label: '文件类型', column: 'file_type', isAsc: false},
    {label: '创建时间', column: 'create_time', isAsc: false},
    {label: '更新时间', column: 'update_time', isAsc: false},
    {label: '创建用户', column: 'create_user', isAsc: false},
    {label: '更新用户', column: 'update_user', isAsc: false},
  ])

  const selectedSortItems = ref([])

  function sortItemChange(selected) {
    queryForm.sortItemList = selected.map(item => ({
      isAsc: item.isAsc,
      column: item.column
    }))
  }

  function isColumnSelected(column) {
    return selectedSortItems.value.some(item => item.column === column)
  }

  function handleTagClose(tag) {
    selectedSortItems.value = selectedSortItems.value.filter(item => item.column !== tag.column)
    sortItemChange(selectedSortItems.value)
  }

  function toggleSortDirection(item) {
    item.isAsc = !item.isAsc
    sortItemChange(selectedSortItems.value)
  }

  watch(selectedSortItems, (newSelectedSortItems, oldSelectedSortItems) => {
    queryForm.sortItemList = newSelectedSortItems.map(item => {
      return {isAsc: item.isAsc, column: item.column}
    })
  }, {deep: true})
  // --------------------------------------------------------
</script>
<style scoped lang="scss">
</style>