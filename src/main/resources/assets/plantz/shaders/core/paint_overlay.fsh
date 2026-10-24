#version 330
#extension GL_ARB_separate_shader_objects : require

uniform sampler2D Sampler0;
uniform sampler2D Sampler1;

layout(location = 6) in vec2 texCoord0;
layout(location = 2) in vec4 vertexColor;

layout(location = 0) out vec4 fragColor;

void main() {
    vec4 base = texture(Sampler0, texCoord0);
    if (base.a < 0.1) discard;

    float n = texture(Sampler1, texCoord0).r;

    float paintAlpha = n * vertexColor.a * base.a;

    fragColor = vec4(vertexColor.rgb, paintAlpha);
}
