export function getAvatarFallbackChar(username, nickname = '') {
  const name = String(username || nickname || '').trim()
  if (!name) return '?'
  return name.charAt(0).toUpperCase()
}
