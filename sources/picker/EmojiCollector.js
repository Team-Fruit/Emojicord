var inputField = temp1;
var emojiScroll = $0;

doneLines = new Set();
emojis = [];
currentName = "<init>";
currentEmojis = [];

function getEmojiId(element, inputField) {
	element.dispatchEvent(new MouseEvent('mouseover', { view: window, cancelable: true, bubbles: true }));
	return inputField.placeholder;
}

function addLine(line) {
	if (!doneLines.has(line.children)) {
		doneLines.add(line.children);
		if (line.innerText.length > 0) {
			emojis.push({
				"name": currentName,
				"emojis": currentEmojis
			});
			currentName = line.innerText;
			currentEmojis = [];
		} else if (line.children.length > 0) {
			var emojiLine = [];
			for (const ele of line.children) {
				(function() {
					var emoji = {
						"dom": ele,
						"id": null
					};
					setTimeout(function() {
						emoji.id = getEmojiId(ele, inputField);
					});
				    emojiLine.push(emoji);
				})();
			}
			currentEmojis.push(emojiLine);
		}
	}
}

emojiScroll.addEventListener("DOMNodeInserted", function(e) {
	addLine(e.target);
}, false);

function addCurrentShown() {
    for (const line of emojiScroll.children) {
        addLine(line);
    }
}