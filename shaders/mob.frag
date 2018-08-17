#version 330 core

layout (location = 0) out vec4 color;

in DATA
{
	vec2 tc;
	vec3 position; //position of pixel being shaded
} fs_in;

uniform vec2 player;
uniform vec2 mouse;
uniform int currentRoom;
uniform int lighting;
uniform int alpha;
uniform sampler2D tex;

void main()
{
	//if mob's not in player's room, don't draw
	if (currentRoom == 0 && lighting == 1) {
		discard;
	}
	float M_PI = 3.14159265358;
	
	color = texture(tex, fs_in.tc); //the color at this texture coordinate
	color = color.xyzw; //color swapping
	//if texture coordinate is transparent, stop shading this pixel with this texture
	if (color.w < 1.0) discard;
	
	vec3 mata = color.xyz; //coefficient of reflectance for ambient lighting
	vec3 matd = color.xyz; //coefficient of reflectance for diffuse lighting
	vec3 mats = color.xyz; //coefficient of reflectance for specular lighting
	
	vec4 temp = color; //copy of color, we can modify and not lose original color if we decide to scrap
	float lightIntensity = 1.0;
	vec3 lightColor = vec3(1.0, 1.0, 1.0);
	float radAtten = 4.0; //how far the diffuse light reaches
	float sightDistance = radAtten + 1.0; //how far objects are visible
	float alphaMod = 1.0;
	float distFromPlayer = (length(player - fs_in.position.xy));	
	
	vec2 v1 = vec2(mouse.xy - player.xy);
	vec2 v2 = (fs_in.position.xy - player.xy);
	float coneAngle = M_PI / 5; //aperture of the spotlight cone
	float lightAngleCos = cos(coneAngle / 2);
	
	float rho = dot(normalize(v2), normalize(v1));
	float dif = 1.0 - lightAngleCos;
	float angAtten = clamp((rho - lightAngleCos) / dif, 0.0, 1.0); //angular attenuation from question post in <http://stackoverflow.com/questions/17487055/spotlight-angular-attenuation-causes-sharp-edges-when-angle-90>
	
	//if the player is in this room
	if (currentRoom == 1) {
		//ambient light
		lightIntensity = 0.05;
		lightColor = vec3(1.0, 1.0, 1.0);
		temp.r = mata.x * lightIntensity * lightColor.x;
		temp.g = mata.y * lightIntensity * lightColor.y;
		temp.b = mata.z * lightIntensity * lightColor.z;
		float radiation = 0.0;
		
		//spot light with radial attenuation
		lightIntensity = 0.0;
		lightColor = vec3(100, 92, 70); //orange color
		lightColor = normalize(lightColor.xyz);
		radiation = -(lightIntensity / radAtten) * distFromPlayer + lightIntensity; //radial atten: see <https://www.desmos.com/calculator/vkg52rekkk> for graph
		if (radiation < 0) radiation = 0;
		temp.r += matd.x * radiation * angAtten * lightColor.x;
		temp.g += matd.y * radiation * angAtten * lightColor.y;
		temp.b += matd.z * radiation * angAtten * lightColor.z;
		
		//spot light with radial attenuation that reaches further based on angAtten, gives it tighter curve at the end
		lightIntensity = 6.0;
		lightColor = vec3(100, 92, 70); //orange color
		lightColor = normalize(lightColor.xyz);
		radiation = -(lightIntensity / (0.7 * angAtten + radAtten)) * distFromPlayer + lightIntensity; //radial atten: see <https://www.desmos.com/calculator/vkg52rekkk> for graph
		if (radiation < 0) radiation = 0;
		temp.r += matd.x * radiation * angAtten * lightColor.x;
		temp.g += matd.y * radiation * angAtten * lightColor.y;
		temp.b += matd.z * radiation * angAtten * lightColor.z;

		//small diffuse light around the player
		lightIntensity = 1.0;
		lightColor = vec3(100, 100, 100); //white color
		lightColor = normalize(lightColor.xyz);
		radiation = -(lightIntensity / 2.3) * distFromPlayer + lightIntensity; //radial atten: see <https://www.desmos.com/calculator/vkg52rekkk> for graph
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
	
	//if lighting is enabled, apply shaders
	if (lighting == 1) color = temp.xyzw; //swap swizzling variables for color swapping
	
}