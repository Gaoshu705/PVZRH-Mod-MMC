<template>
  <div class="vo-list">
    <div class="query" v-if="mode === 'edit'">
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
              <div class="query-placeholder">角色名称:</div>
              <div class="query-input">
                <el-input v-model="queryForm.roleName" placeholder="角色名称"/>
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
        <div v-if="mode === 'show'" class="pagination-table">
          <el-table
              :data="paginatedData"
              :row-key="(row) => row.roleId"
              border
              style="width: 100%"
          >
            <el-table-column prop="roleId" label="角色ID" min-width="120" />
            <el-table-column prop="roleName" label="角色名称" min-width="120" />
            <el-table-column prop="createTime" label="创建时间" min-width="120" />
            <el-table-column prop="updateTime" label="更新时间" min-width="120" />
            <el-table-column prop="createUser" label="创建用户" min-width="120" />
            <el-table-column prop="updateUser" label="更新用户" min-width="120" />
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
        <div v-if="mode === 'edit'" class="pagination-table">
          <el-table
              :data="tableData"
              :row-key="(row) => row.roleId"
              @selection-change="handleSelectionChange"
              border
              ref="multipleTable"
              style="width: 100%"
          >
            <el-table-column type="selection" width="42"/>
            <el-table-column prop="roleId" label="角色ID" min-width="120" />
            <el-table-column prop="roleName" label="角色名称" min-width="120" />
            <el-table-column prop="createTime" label="创建时间" min-width="120" />
            <el-table-column prop="updateTime" label="更新时间" min-width="120" />
            <el-table-column prop="createUser" label="创建用户" min-width="120" />
            <el-table-column prop="updateUser" label="更新用户" min-width="120" />
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
</template>

<script setup>
  import {ref, reactive, onMounted, watch, nextTick, computed} from 'vue'
  import {roleApi} from '@/api/role-api'

  // ------------------------ 属性 ------------------------
  const props = defineProps({
    mode: {
      type: String,
      default: 'show' // 'edit
    },
    oldData: {
      type: Array,
      default: []
    }
  })

  const multipleTable = ref(null);
  // ------------------------ 多选 -----------------------------
  const selectedRowList = ref([])

  function handleSelectionChange(val) {
    selectedRowList.value = val
  }

  // 切换行的选中状态
  function toggleSelection(rows) {
    if (rows) {
      rows.forEach(row => {
        // 使用 nextTick 确保在 DOM 更新后执行
        nextTick(() => {
          multipleTable.value.toggleRowSelection(row, true);
        });
      });
    } else {
      multipleTable.value.clearSelection();
    }
  }

  // 默认选中 oldData 中的行
  function defaultSelectOldData() {
    if (props.oldData != null && props.oldData.length > 0) {
      const selectedRows = tableData.value.filter(row =>
          props.oldData.some(oldRow => oldRow.roleId === row.roleId)
      );
      toggleSelection(selectedRows);
    }
  }

  // 定义一个方法来获取 selectedRowList
  function getSelectedRowList() {
    return selectedRowList.value
  }

  // 暴露给父组件
  defineExpose({
    getSelectedRowList
  })
  // ------------------------ 枚举量 ------------------------
  // --------------------------------------------------------

  // 查询数据表单和方法
  const queryFormState = {
    pageNum: 1,
    pageSize: 10,
    roleName: null,
    sortItemList: []
  }
  const queryForm = reactive({...queryFormState})
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
    if (props.mode === 'show') {
      if (props.oldData == null) return
      total.value = props.oldData.length
      return
    }
    try {
      let queryResult = await roleApi.page(queryForm)
      tableData.value = queryResult.data.list
      total.value = queryResult.data.total
      // 默认选中 oldData 中的行
      defaultSelectOldData();
    } catch (e) {
      console.log(e)
    }
  }

  function handleSizeChange(newSize) {
    queryForm.pageSize = newSize
    onSearch()
  }


  onMounted(queryData)
  // ------------------------ show展示 ------------------------
  // 计算当前页的数据
  const paginatedData = computed(() => {
    if (props.oldData == null) return []
    const start = (queryForm.pageNum - 1) * queryForm.pageSize;
    const end = start + queryForm.pageSize;
    return props.oldData.slice(start, end);
  });
  // --------------------------------------------------------
  // -------------------------- 排序字段 -------------------------
  const sortItemOptions = ref([
    {label: '角色ID', column: 'role_id', isAsc: false},
    {label: '角色名称', column: 'role_name', isAsc: false},
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