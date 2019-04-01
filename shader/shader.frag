#version 450
#extension GL_ARB_separate_shader_objects : enable

layout(set = 0, binding = 0) uniform sampler samp;
layout(set = 0, binding = 1) uniform texture2D textures[8];

layout(location=0) in vec3 outColor;
layout(location=1) in vec2 texCord;

layout(location=0) out vec4 color;

void main(void) {
  color = texture(sampler2D(textures[0], samp), texCord);
}