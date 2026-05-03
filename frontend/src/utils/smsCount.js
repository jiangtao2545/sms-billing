const BMP_MAX_CODE_POINT = 0xFFFF
const SINGLE_SMS_MAX_CHARS = 70
const MULTI_SMS_SEGMENT_CHARS = 67

function countChars(content) {
  if (!content) return 0
  let count = 0
  for (let i = 0; i < content.length;) {
    const code = content.codePointAt(i)
    if (code > BMP_MAX_CODE_POINT) {
      count += 2
      i += 2
    } else {
      count += 1
      i += 1
    }
  }
  return count
}

function countBillingSegments(chars) {
  if (chars <= SINGLE_SMS_MAX_CHARS) return 1
  return Math.ceil(chars / MULTI_SMS_SEGMENT_CHARS)
}

function calcFee(segments, price) {
  return (segments * price).toFixed(4)
}

export { countChars, countBillingSegments, calcFee }
