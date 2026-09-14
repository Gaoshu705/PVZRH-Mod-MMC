export function getAvatarFallbackChar(username, nickname = '') {
  const name = String(nickname || username || '').trim()
  if (!name) return '?'
  return name.charAt(0).toUpperCase()
}
