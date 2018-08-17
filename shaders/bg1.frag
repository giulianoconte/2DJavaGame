#version 330 core

layout (location = 0) out vec4 color;

in DATA
{
	vec2 tc;
	vec3 position; //position of pixel being shaded
} fs_in;

uniform vec2 player;
uniform int currentRoom;
uniform int lighting;
uniform int alpha;
uniform sampler2D tex;

void main()
{
	color = texture(tex, fs_in.tc); //the color at this texture coordinate
	vec3 mata = color.xyz; //coefficient of reflectance for ambient lighting
	vec3 matd = color.xyz; //coefficient of reflectance for diffuse lighting
	vec3 mats = color.xyz; //coefficient of reflectance for specular lighting
	
	vec4 temp = color; //copy of color, we can modify and not lose original color if we decide to scrap
	float lightIntensity = 1.0;
	vec3 lightColor = vec3(1.0, 1.0, 1.0);
	float sightDistance = 4.5; //how far objects are visible
	float radAtten = 3.5; //how far the diffuse light reaches
	float alphaMod = 1.0;
	float distFromPlayer = (length(player - fs_in.position.xy));
	
	//if the player is in this room
	if (currentRoom == 1) {
		//ambient light
		lightIntensity = 0.05;
		lightColor = vec3(1.0, 1.0, 1.0);
		temp.r = mata.x * lightIntensity * lightColor.x;
		temp.g = mata.y * lightIntensity * lightColor.y;
		temp.b = mata.z * lightIntensity * lightColor.z;
		float radiation = 0.0;
		
		//diffuse light with radial attenuation
		lightIntensity = 2.0;
		lightColor = vec3(100, 88, 68); //orange color
		lightColor = normalize(lightColor.xyz);
		radiation = -(lightIntensity / radAtten) * distFromPlayer + lightIntensity; //radial atten: see <https://www.desmos.com/calculator/vkg52rekkk> for graph
		if (radiation < 0) radiation = 0;
		temp.r += matd.x * radiation * lightColor.x;
		temp.g += matd.y * radiation * lightColor.y;
		temp.b += matd.z * radiation * lightColor.z;
		
		//field of view
		if (alpha == 0) {
			if (distFromPlayer < 3.3) distFromPlayer = 3.3;
			alphaMod = 1.0 * pow((distFromPlayer - (sightDistance - 2)), -2);
		}
		if (alphaMod < 0.0) alphaMod = 0.0;
		if (alphaMod >= 1.0) alphaMod = 1.0;
		temp.w *= alphaMod;
	}
	if (currentRoom == 0) {
		temp = color.xyzw;
		alphaMod = 0.15;
		temp.w *= alphaMod;
	}
	//color swapping
	color = color.xyzw;
	//if lighting is enabled, apply shaders
	if (lighting == 1) color = temp;
	
}