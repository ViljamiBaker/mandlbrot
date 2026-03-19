#version 330 core

layout(location = 0) in vec2 aPos;

out vec2 FragPos;

uniform vec2 Scale;
uniform vec2 Offset;
uniform float ScreenRatio;

void main()
{
    FragPos = aPos;
    gl_Position = vec4(aPos.x*Scale.x/ScreenRatio + Offset.x, aPos.y*Scale.y + Offset.y, 0, 1.0);
}