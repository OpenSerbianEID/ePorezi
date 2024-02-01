#!/usr/bin/env python3
from xml.dom import minidom

main_path = 'out/ePorezi.app/Contents/Info.plist'
main_doc = minidom.parse(main_path)

append_str = '''
<doc>
    <key>CFBundleURLTypes</key>
    <array>
      <dict>
        <key>CFBundleURLName</key>
        <string>ePorezi</string>
        <key>CFBundleURLSchemes</key>
        <array>
          <string>eporezi</string>
        </array>
      </dict>
    </array>
</doc>
'''
append_doc = minidom.parseString(append_str)

for append_node in list(append_doc.childNodes[0].childNodes):
	main_doc.childNodes[0].childNodes[1].appendChild(append_node)

with open(main_path, 'w') as f:
	f.write(main_doc.toxml())
