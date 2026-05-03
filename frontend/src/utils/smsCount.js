function countChars(content) {
  if (!content) return 0
  let count = 0
  for (let i = 0; i < content.length;) {
    const code = content.codePointAt(i)
    if (code > 0xFFFF) {
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
  if (chars <= 70) return 1
  return Math.ceil(chars / 67)
}

function calcFee(segments, price) {
  return (segments * price).toFixed(4)
}

export { countChars, countBillingSegments, calcFee }
