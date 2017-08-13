function splitTextIntoLines(text, thresholdCharCount) {
    if (text.length < thresholdCharCount) {
        return [text]
    }

    const words = text.split(" ")
    const result = splitParts(words,
        (word) => word.length + 1, // one for space
        thresholdCharCount)

    return result.map(words => words.join(" "))
}

function splitParts(parts, lengthFunc, thresholdCharCount) {
    const result = []
    let runningLength = 0
    let runningWords = []

    parts.forEach(part => {
        runningLength += lengthFunc(part)
        runningWords.push(part)

        if (runningLength >= thresholdCharCount) {
            flush()
        }
    })

    flush()

    return result

    function flush() {
        if (runningWords.length) {
            result.push(runningWords)
        }

        runningWords = []
        runningLength = 0
    }
}

export {splitTextIntoLines, splitParts}