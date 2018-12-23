#version 450
#extension GL_ARB_separate_shader_objects : enable

in vec3 outColor;

layout(location=0) out vec4 color;

void main(void) {
  color = vec4(outColor, 1.0);
}