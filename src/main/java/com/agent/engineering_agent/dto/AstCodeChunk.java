package com.agent.engineering_agent.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AstCodeChunk {
  String code;
  String className;
  String methodName;
}
