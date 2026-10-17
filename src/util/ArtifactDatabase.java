package util;

import model.Artifact;
import model.Artifact.Rarity;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ArtifactDatabase {

    private static final List<Artifact>
            ALL_ARTIFACTS =
            new ArrayList<>();

    static {

        //=========================
        // OLD_CELLAR
        //=========================

        ALL_ARTIFACTS.add(new Artifact(
                "LT001",
                "Rusty Iron Sword",
                "An iron sword of unknown age, heavily rusted, possibly from an ancient battlefield.",
                "OLD_CELLAR",
                "Weapon",
                200,
                0.28,
                150,
                0.60,
                800,
                10,
                Rarity.LOW));

        ALL_ARTIFACTS.add(new Artifact(
                "LT002",
                "Blue and White Porcelain Shard",
                "A fragment suspected to be from Ming Dynasty blue and white porcelain.",
                "OLD_CELLAR",
                "Ceramic",
                200,
                0.24,
                200,
                0.45,
                1200,
                5,
                Rarity.LOW));

        ALL_ARTIFACTS.add(new Artifact(
                "LT003",
                "Muddy Clay Cup",
                "A clay cup excavated from the soil.",
                "OLD_CELLAR",
                "Ceramic",
                200,
                0.16,
                200,
                0.20,
                8000,
                10,
                Rarity.LOW));

        ALL_ARTIFACTS.add(new Artifact(
                "LT004",
                "String of Copper Coins",
                "A string of copper coins.",
                "OLD_CELLAR",
                "Relic",
                200,
                0.18,
                100,
                0.70,
                600,
                20,
                Rarity.LOW));

        ALL_ARTIFACTS.add(new Artifact(
                "LT005",
                "Wooden Buddha Statue",
                "A palm-sized Buddha statue.",
                "OLD_CELLAR",
                "Relic",
                200,
                0.14,
                150,
                0.50,
                2000,
                30,
                Rarity.LOW));


        //=========================
        // BLACK_MARKET_SHIP
        //=========================

        ALL_ARTIFACTS.add(new Artifact(
                "MT001",
                "Medieval Armor Piece",
                "European armor piece.",
                "BLACK_MARKET_SHIP",
                "Armor",
                1500,
                0.26,
                5000,
                0.70,
                35000,
                500,
                Rarity.MID));

        ALL_ARTIFACTS.add(new Artifact(
                "MT002",
                "Unknown Oil Painting",
                "17th century landscape.",
                "BLACK_MARKET_SHIP",
                "Artwork",
                1500,
                0.21,
                8000,
                0.35,
                80000,
                200,
                Rarity.MID));

        ALL_ARTIFACTS.add(new Artifact(
                "MT003",
                "Egyptian Pharaoh Mask Replica",
                "Golden pharaoh mask.",
                "BLACK_MARKET_SHIP",
                "Relic",
                1500,
                0.14,
                10000,
                0.20,
                200000,
                300,
                Rarity.MID));

        ALL_ARTIFACTS.add(new Artifact(
                "MT004",
                "Roman Glass Vessel",
                "Ancient Roman bottle.",
                "BLACK_MARKET_SHIP",
                "Relic",
                1500,
                0.23,
                4000,
                0.60,
                25000,
                100,
                Rarity.MID));

        ALL_ARTIFACTS.add(new Artifact(
                "MT005",
                "Edo Period Katana",
                "Japanese samurai sword.",
                "BLACK_MARKET_SHIP",
                "Weapon",
                1500,
                0.16,
                9000,
                0.55,
                60000,
                400,
                Rarity.MID));


        //=========================
        // ATLANTIS_RUINS
        //=========================

        ALL_ARTIFACTS.add(new Artifact(
                "MY001",
                "Thor Hammer",
                "Legendary warhammer.",
                "ATLANTIS_RUINS",
                "Weapon",
                10000,
                0.30,
                80000,
                0.50,
                500000,
                2000,
                Rarity.MYTH));

        ALL_ARTIFACTS.add(new Artifact(
                "MY002",
                "Atlantis Crown",
                "Ancient crown.",
                "ATLANTIS_RUINS",
                "Jewelry",
                10000,
                0.15,
                300000,
                0.25,
                999999,
                10000,
                Rarity.MYTH));

        ALL_ARTIFACTS.add(new Artifact(
                "MY003",
                "Poseidon Trident Fragment",
                "Ancient trident fragment.",
                "ATLANTIS_RUINS",
                "Weapon",
                10000,
                0.25,
                100000,
                0.40,
                700000,
                5000,
                Rarity.MYTH));

        ALL_ARTIFACTS.add(new Artifact(
                "MY004",
                "Mermaid Tear Sapphire",
                "Deep sea sapphire.",
                "ATLANTIS_RUINS",
                "Jewelry",
                10000,
                0.20,
                150000,
                0.35,
                800000,
                3000,
                Rarity.MYTH));

        ALL_ARTIFACTS.add(new Artifact(
                "MY005",
                "Pangu Mythical Axe",
                "Legendary axe.",
                "ATLANTIS_RUINS",
                "Weapon",
                10000,
                0.10,
                500000,
                0.15,
                1000000,
                20000,
                Rarity.MYTH));
    }


    public static List<Artifact>
    getPool(String poolName)
    {
        return ALL_ARTIFACTS
                .stream()
                .filter(a->
                        a.getPool()
                         .equals(poolName))
                .collect(Collectors.toList());
    }


    public static Artifact
    getById(String id)
    {
        return ALL_ARTIFACTS
                .stream()
                .filter(a->
                        a.getId()
                         .equals(id))
                .findFirst()
                .orElse(null);
    }


    public static List<Artifact>
    getByRarity(
            Artifact.Rarity rarity)
    {
        return ALL_ARTIFACTS
                .stream()
                .filter(a->
                        a.getRarity()==rarity)
                .collect(Collectors.toList());
    }


    public static List<Artifact>
    getAll()
    {
        return new ArrayList<>(
                ALL_ARTIFACTS);
    }

}