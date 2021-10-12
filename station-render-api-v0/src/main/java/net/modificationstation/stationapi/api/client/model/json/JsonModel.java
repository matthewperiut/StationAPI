package net.modificationstation.stationapi.api.client.model.json;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.InstanceCreator;
import net.minecraft.client.resource.TexturePack;
import net.modificationstation.stationapi.api.client.model.BasicBakedModel;
import net.modificationstation.stationapi.api.client.model.Model;
import net.modificationstation.stationapi.api.client.model.Vertex;
import net.modificationstation.stationapi.api.client.registry.ModelRegistry;
import net.modificationstation.stationapi.api.client.texture.atlas.Atlas;
import net.modificationstation.stationapi.api.client.texture.atlas.Atlases;
import net.modificationstation.stationapi.api.registry.Identifier;
import net.modificationstation.stationapi.api.resource.ResourceManager;
import net.modificationstation.stationapi.api.util.math.Direction;
import net.modificationstation.stationapi.impl.client.model.GuiLightType;
import net.modificationstation.stationapi.impl.client.model.JsonCuboidData;
import net.modificationstation.stationapi.impl.client.model.JsonFaceData;
import net.modificationstation.stationapi.impl.client.model.JsonModelData;

import java.io.*;
import java.lang.reflect.*;
import java.nio.charset.*;
import java.util.*;
import java.util.stream.*;

import static net.modificationstation.stationapi.api.StationAPI.MODID;
import static net.modificationstation.stationapi.api.client.texture.atlas.JsonModelAtlas.MISSING;
import static net.modificationstation.stationapi.api.registry.Identifier.of;
import static net.modificationstation.stationapi.api.util.math.Direction.values;

public final class JsonModel extends Model {

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static final Gson GSON = new GsonBuilder().registerTypeAdapter(EnumMap.class, (InstanceCreator<EnumMap>) type -> new EnumMap((Class) ((ParameterizedType) type).getActualTypeArguments()[0])).create();

    private JsonModelData data;
    private ImmutableMap<String, Atlas.Sprite> textures;

    public static JsonModel get(final Identifier identifier) {
        return get(identifier, JsonModel::new);
    }

    private JsonModel(final Identifier identifier) {
        super(identifier, "json");
    }

    @Override
    public void reloadFromTexturePack(final TexturePack newTexturePack) {
        invalidated = true;
        InputStream stream = newTexturePack.getResourceAsStream(modelPath);
        if (stream == null) {
            ModelRegistry.INSTANCE.unregister(id);
            data = null;
            textures = null;
        } else {
            data = GSON.fromJson(new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8)).lines().collect(Collectors.joining("\n")), JsonModelData.class);
            List<JsonModelData> inheritance = new ArrayList<>();
            {
                JsonModelData parentData = data;
                inheritance.add(parentData);
                while (parentData.parent != null)
                    inheritance.add(
                            parentData = GSON.fromJson(
                                    new BufferedReader(
                                            new InputStreamReader(
                                                    newTexturePack.getResourceAsStream(
                                                            ResourceManager.parsePath(
                                                                    Identifier.of(parentData.parent),
                                                                    "/" + MODID + "/models", "json")
                                                    ),
                                                    StandardCharsets.UTF_8
                                            )
                                    ).lines().collect(Collectors.joining("\n")), JsonModelData.class)
                    );
                Collections.reverse(inheritance);
            }
            Map<String, String> textures = new HashMap<>();
            List<JsonCuboidData> elements = new ArrayList<>();
            inheritance.forEach(parentData -> {
                if (parentData.textures != null)
                    textures.putAll(parentData.textures);
                if (parentData.elements != null) {
                    elements.clear();
                    elements.addAll(parentData.elements);
                }
            });
            data.textures = textures;
            data.elements = elements;
            ImmutableMap.Builder<String, Atlas.Sprite> texturesBuilder = ImmutableMap.builder();
            data.textures.forEach((textureId, texturePath) -> {
                while (texturePath.startsWith("#")) texturePath = data.textures.get(texturePath.substring(1));
                texturesBuilder.put("#" + textureId, Atlases.getStationJsonModels().addTexture(of(texturePath)));
            });
            this.textures = texturesBuilder.build();
            data.elements.forEach(cuboid -> {
                cuboid.postprocess();
                cuboid.faces.values().forEach(face -> face.postprocess(this.textures.getOrDefault(face.textureId, Atlases.getStationJsonModels().addTexture(MISSING))));
            });
            updateUVs();
        }
    }

    public void updateUVs() {
        invalidated = true;
        if (data != null)
            data.elements.forEach(cuboid -> cuboid.faces.values().forEach(JsonFaceData::updateUVs));
    }

    @Override
    protected BasicBakedModel bake() {
        Map<Direction, ImmutableList.Builder<Vertex>> faceVertexesBuilder = new EnumMap<>(Direction.class);
        Arrays.stream(values()).forEach(direction -> faceVertexesBuilder.put(direction, ImmutableList.builder()));
        ImmutableList.Builder<Vertex> vertexes = ImmutableList.builder();
        data.elements.forEach(cuboid -> {
            double[]
                    from = cuboid.from,
                    to = cuboid.to;
            double
                    xFrom = from[0],
                    yFrom = from[1],
                    zFrom = from[2],
                    xTo = to[0],
                    yTo = to[1],
                    zTo = to[2];
            Map<Direction, JsonFaceData> faces = cuboid.faces;
            boolean shade = cuboid.isShade();
            faces.forEach((direction, face) -> {
                boolean absentCullface = face.cullface == null;
                Direction lightingFace = absentCullface ? direction : face.cullface;
                ImmutableList.Builder<Vertex> v = absentCullface ? vertexes : faceVertexesBuilder.get(face.cullface);
                face.updateUVs();
                double[] uv = face.getUv();
                switch (direction) {
                    case DOWN:
                        v.add(Vertex.get(xFrom, yFrom, zTo, uv[4], uv[7], lightingFace, shade));
                        v.add(Vertex.get(xFrom, yFrom, zFrom, uv[0], uv[1], lightingFace, shade));
                        v.add(Vertex.get(xTo, yFrom, zFrom, uv[6], uv[5], lightingFace, shade));
                        v.add(Vertex.get(xTo, yFrom, zTo, uv[2], uv[3], lightingFace, shade));
                        break;
                    case UP:
                        v.add(Vertex.get(xTo, yTo, zTo, uv[2], uv[3], lightingFace, shade));
                        v.add(Vertex.get(xTo, yTo, zFrom, uv[6], uv[5], lightingFace, shade));
                        v.add(Vertex.get(xFrom, yTo, zFrom, uv[0], uv[1], lightingFace, shade));
                        v.add(Vertex.get(xFrom, yTo, zTo, uv[4], uv[7], lightingFace, shade));
                        break;
                    case EAST:
                        v.add(Vertex.get(xFrom, yTo, zFrom, uv[2], uv[1], lightingFace, shade));
                        v.add(Vertex.get(xTo, yTo, zFrom, uv[0], uv[1], lightingFace, shade));
                        v.add(Vertex.get(xTo, yFrom, zFrom, uv[0], uv[3], lightingFace, shade));
                        v.add(Vertex.get(xFrom, yFrom, zFrom, uv[2], uv[3], lightingFace, shade));
                        break;
                    case WEST:
                        v.add(Vertex.get(xFrom, yTo, zTo, uv[0], uv[1], lightingFace, shade));
                        v.add(Vertex.get(xFrom, yFrom, zTo, uv[0], uv[3], lightingFace, shade));
                        v.add(Vertex.get(xTo, yFrom, zTo, uv[2], uv[3], lightingFace, shade));
                        v.add(Vertex.get(xTo, yTo, zTo, uv[2], uv[1], lightingFace, shade));
                        break;
                    case NORTH:
                        v.add(Vertex.get(xFrom, yTo, zTo, uv[2], uv[1], lightingFace, shade));
                        v.add(Vertex.get(xFrom, yTo, zFrom, uv[0], uv[1], lightingFace, shade));
                        v.add(Vertex.get(xFrom, yFrom, zFrom, uv[0], uv[3], lightingFace, shade));
                        v.add(Vertex.get(xFrom, yFrom, zTo, uv[2], uv[3], lightingFace, shade));
                        break;
                    case SOUTH:
                        v.add(Vertex.get(xTo, yFrom, zTo, uv[0], uv[3], lightingFace, shade));
                        v.add(Vertex.get(xTo, yFrom, zFrom, uv[2], uv[3], lightingFace, shade));
                        v.add(Vertex.get(xTo, yTo, zFrom, uv[2], uv[1], lightingFace, shade));
                        v.add(Vertex.get(xTo, yTo, zTo, uv[0], uv[1], lightingFace, shade));
                        break;
                }
            });
        });
        ImmutableMap.Builder<Direction, ImmutableList<Vertex>> faceVertexes = ImmutableMap.builder();
        faceVertexesBuilder.forEach((direction, faceQuadPointBuilder) -> faceVertexes.put(direction, faceQuadPointBuilder.build()));
        return new BasicBakedModel.Builder()
                .faceVertexes(Maps.immutableEnumMap(faceVertexes.build()))
                .vertexes(vertexes.build())
                .useAO(data.isAmbientocclusion())
                .isSideLit(data.gui_light == GuiLightType.SIDE)
                .sprite(textures.get("#particle"))
                .build();
    }
}