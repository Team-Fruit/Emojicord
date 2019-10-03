emojiResult = [];

function processEmojiTable() {
    for (const emojiGroup of emojis) {
        var emojiName = emojiGroup.name;
        var emojiList = emojiGroup.emojis;
        var emojiList0 = [];
        for (const emojiLine of emojiList) {
            var emojiLine0 = [];
            for (const emoji of emojiLine) {
                emojiLine0.push(emoji.id);
            }
            emojiList0.push(emojiLine0);
        }
        var emojiGroup0 = {
            "name": emojiName,
            "emojis": emojiList0
        };
        emojiResult.push(emojiGroup0);
    }
    return JSON.stringify({"category": emojiResult}, null , "  ");
}

function processEmoji() {
    for (const emojiGroup of emojis) {
        var emojiName = emojiGroup.name;
        var emojiList = emojiGroup.emojis;
        var emojiList0 = [];
        for (const emojiLine of emojiList) {
            for (const emoji of emojiLine) {
                emojiList0.push(emoji.id);
            }
        }
        var emojiGroup0 = {
            "name": emojiName,
            "emojis": emojiList0
        };
        emojiResult.push(emojiGroup0);
    }
    return JSON.stringify({"category": emojiResult}, null , "  ");
}
