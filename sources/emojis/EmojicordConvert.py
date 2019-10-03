import json
import codecs
import yaml
import requests

response = requests.get(
    'https://static.emzi0767.com/misc/discordEmojiMap.json')

if response.status_code == 200:
    jsn = response.json()

# with codecs.open('discordEmojiMap.json', 'r', 'utf-8-sig') as f:
#     jsn = json.load(f)

output = {
    'location': 'https://twemoji.maxcdn.com/2/72x72/',
    'version': jsn['version'],
    'versionTimestamp': jsn['versionTimestamp'],
    'emojis': [],
}
for i, data in enumerate(jsn['emojiDefinitions']):
    filename = '-'.join([format(codepoint, 'x') for codepoint in data['utf32codepoints']])
    output['emojis'].append({
        'name': data['primaryName'],
        'strings': data['names'],
        'location': f'{filename}.png',
        'surrogates': data['surrogates'],
    })

with codecs.open('emojis.yml', 'w', 'utf-8') as f:
    yaml.dump(output, f, encoding='utf8', allow_unicode=True)
