#version 450
#extension GL_ARB_separate_shader_objects : enable

layout(location=0) in vec2 position;
layout(location=1) in vec3 color;
layout(location=2) in vec2 inTexCords;
layout(location=3) in uint inTexIndex;

layout(location=0) out vec3 outColor;
layout(location=1) out vec2 outTexCords;
layout(location=2) out uint outTexIndex;

void main(void) {
  outColor = color;
  outTexCords = inTexCords;
  outTexIndex = inTexIndex;
  gl_Position = vec4(position, 0.0, 1.0);
}