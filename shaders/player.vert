#version 330 core

layout (location = 0) in vec4 position;
layout (location = 1) in vec2 tc;

uniform mat4 pr_matrix;
uniform mat4 vw_matrix = mat4(1.0);	//not necessary 
uniform mat4 ml_matrix = mat4(1.0);
uniform int reflect;

out DATA
{
	vec2 tc;
} vs_out;

void main()
{
	float yscale = 1.0;
	if (reflect == 1) yscale = -1.0;
	mat4 scale = mat4(
		yscale, 0.0, 0.0, 0.0,
		0.0, yscale, 0.0, 0.0,
		0.0, 0.0, 1.0, 0.0,
		0.0, 0.0, 0.0, 1.0
	);
	gl_Position =  pr_matrix * vw_matrix * ml_matrix * scale * position;
	vs_out.tc = tc;
}