import {useMenuStore} from "@/stores/menu.js";

export function hasPerm(value) {
  const codes = useMenuStore().codes
  if (!value || !codes) return false
  if (Array.isArray(value) && value.length > 0) {
    for (let each of value) {
      if (!codes.includes(each)) return false
    }
    return true
  } else {
    return codes.includes(value);
  }
}