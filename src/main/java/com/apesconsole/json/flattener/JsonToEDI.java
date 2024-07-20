package com.apesconsole.json.flattener;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class JsonToEDI {

	@Autowired
	private FileDao fileDao;

	public void generateEdi() {
		try {
			long start = System.currentTimeMillis();
			ObjectMapper mapper = new ObjectMapper();
			JsonNode root = mapper.readTree(fileDao.readFile("test-data.json"));

			StringBuilder ediString = new StringBuilder();
			parseLevel(root, ediString, 1);

			// Remove the trailing level separator if present
			if (ediString.length() > 0 && ediString.charAt(ediString.length() - 1) == ';') {
				ediString.deleteCharAt(ediString.length() - 1);
			}
			long end = System.currentTimeMillis();
			log.info("Parse Completed in: " + (end - start));
			start = System.currentTimeMillis();
			fileDao.writeFile("output.edi", ediString.toString());
			end = System.currentTimeMillis();
			log.info("EDI Written in:" + (end - start));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void parseLevel(JsonNode node, StringBuilder ediString, int level) {
		if (node == null) {
			return;
		}
		if (node.isArray()) {
			for (int i = 0; i < node.size(); i++) {
				JsonNode child = node.get(i);
				appendNodeData(child, ediString);

				// Append a sibling separator if not the last sibling
				if (i < node.size() - 1) {
					ediString.append("#");
				}
			}

			// Append a level separator
			ediString.append(";");

			for (JsonNode child : node) {
				// Recursive call to parse the next level
				JsonNode children = null;
				if (child.has("rxswins")) {
					children = child.get("rxswins");
				} else if (child.has("configurations")) {
					children = child.get("configurations");
				} else if (child.has("ecuVariants")) {
					children = child.get("ecuVariants");
				} else if (child.has("parts")) {
					children = child.get("parts");
				}
				parseLevel(children, ediString, level + 1);
			}
		} else {
            appendNodeData(node, ediString);
            
			JsonNode children = null;
			if (node.has("rxswins")) {
				children = node.get("rxswins");
			} else if (node.has("configurations")) {
				children = node.get("configurations");
			} else if (node.has("ecuVariants")) {
				children = node.get("ecuVariants");
			} else if (node.has("parts")) {
				children = node.get("parts");
			}
			parseLevel(children, ediString, level + 1);
		}
	}
	

    private static void appendNodeData(JsonNode node, StringBuilder ediString) {
        String uuid = node.get("uuid").asText();
        String levelType = node.get("levelType").asText();
        boolean status = node.get("status").asBoolean();
        
        ediString.append(uuid).append("|")
                 .append(levelType).append("|")
                 .append(status ? "1" : "0");
    }
}
