import json
import codecs
import requests

response = requests.get(
    'https://static.emzi0767.com/misc/discordEmojiMap.json')

if response.status_code == 200:
    jsn = response.json()

# with codecs.open('discordEmojiMap.json', 'r', 'utf-8-sig') as f:
#     jsn = json.load(f)

output = {
    'version': jsn['version'],
    'versionTimestamp': jsn['versionTimestamp'],
    'groups': [
        {
            'location': 'https://twemoji.maxcdn.com/2/72x72/',
            'emojis': [],
        }
    ],
}
for i, data in enumerate(jsn['emojiDefinitions']):
    filename = '-'.join([format(codepoint, 'x') for codepoint in data['utf32codepoints']])
    output['groups'][0]['emojis'].append({
        'name': data['primaryName'],
        'strings': data['names'],
        'location': f'{filename}.png',
        'surrogates': data['surrogates'],
    })

with codecs.open('emojis.json', 'w', 'utf-8') as f:
    json.dump(output, f, indent=2, ensure_ascii=False)

with codecs.open('emojis.min.json', 'w', 'utf-8') as f:
    json.dump(output, f, ensure_ascii=False)
