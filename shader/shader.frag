#version 450
#extension GL_ARB_separate_shader_objects : enable

layout(set = 0, binding = 0) uniform sampler samp;
layout(set = 0, binding = 1) uniform texture2D textures[8];

layout(location=0) in vec3 outColor;
layout(location=1) in vec2 texCord;

layout(location=0) out vec4 color;

void main(void) {
  //color = vec4(outColor, 1.0);
  color = texture(sampler2D(textures[0], samp), texCord);
  //color = vec4(texCord, 0.0, 1.0);
  //color = vec4(1.0, 0.0, 0.0, 1.0);
}