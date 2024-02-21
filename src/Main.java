import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        String commande;

        System.out.println("Entrer le nombre, le nom et la valeur d'un produit acheté (entrer une ligne vide pour éditer la facture) : ");

        BigDecimal additionTaxe = new BigDecimal(0); //cout ttc - cout ht

        BigDecimal totalTTC = new BigDecimal(0);

        StringBuffer sb = new StringBuffer();

        while(sc.hasNextLine() && !((commande = sc.nextLine()).isEmpty())){

            //Une ligne de commande valide :
            //      - commence par un chiffre : ^\d+
            //      - est suivi par le nom du produit (des lettres et des espaces mélangés) <-- peut contenir le terme "importée à" ou "importé à"
            //      - est suivi par "à "
            //      - contient un chiffre (partie entiére + .+ partie décimal)
            //      - se termine par €
            //TODO dans l'énoncé la ligne commence par "* " on ne le prend pas pour l'instant, à voir si c'est nécessaire
            Pattern pattern = Pattern.compile("^(?<nombreProduit>\\d+)(?<nomProduit>([A-Za-zÀ-ÿ]+|\\s+)+à\\s)(?<coutProduitHT>\\d+.?\\d+)€\\z");

            Matcher matcher = pattern.matcher(commande);

            boolean validInput = matcher.matches();

            if(validInput){
                String nomProduit = matcher.group("nomProduit");


                BigDecimal tauxTva = new BigDecimal(20);
                BigDecimal tauxSupplement = new BigDecimal(0);


                if (nomProduit.contains("chocolat") || nomProduit.contains("pilule")) { //les seuls produits reconnu comme nourriture et médicaments pour le moment
                    //aucune taxe sur la valeur ajoutée (TVA) n'est appliquée sur les produits de premières nécessités, à savoir la nourriture et les médicaments.
                    tauxTva = new BigDecimal(0);
                } else if (nomProduit.contains("livre")) {
                    //Une taxe sur la valeur ajoutée (TVA) réduite de 10% est appliquée sur les livres.
                    tauxTva = new BigDecimal(10);
                }

                //Une taxe additionnelle de 5% est appliquée sur les produits importés, sans exception (la nourriture et les médicaments sont compris).
                if (nomProduit.contains("importé")) {
                    tauxSupplement = new BigDecimal(5);
                }

                BigDecimal coutProduitHT = new BigDecimal(matcher.group("coutProduitHT"));

                BigDecimal nombreProduit = new BigDecimal(matcher.group("nombreProduit"));

                BigDecimal coutProduitTTC = nombreProduit.multiply(calculTTC(coutProduitHT, tauxTva, tauxSupplement));

                additionTaxe = additionTaxe.add(coutProduitTTC.subtract(nombreProduit.multiply(coutProduitHT)));
                totalTTC = totalTTC.add(coutProduitTTC);


                //en sortie, par ligne d'entré on a :
                //  - l'entrée
                //  - " : " + le nombre de produit multiplié par leurs cout HT + TVA
                //TODO à voir : dans l'exercice on trouve des 0 qui trainent à certains moments et pas à d'autres
                //TODO à voir : dans le panier 1  "TTC" est affiché après les chiffres mais pas dans les autres paniers
                sb.append(commande + " : " + coutProduitTTC.stripTrailingZeros() + "€" ).append(System.lineSeparator());;
            }

            System.out.println("Entrer le nombre, le nom et la valeur d'un produit acheté (entrer une ligne vide pour éditer la facture) : ");


        }

            System.out.println(sb);
            //en sortie, en une fois on a :
            System.out.println("Montant des taxes : " + additionTaxe + "€");
            System.out.println("");
            System.out.println("Total : " + totalTTC + "€");


    }

    private static BigDecimal calculTTC(BigDecimal coutProduitHT, BigDecimal tauxTva, BigDecimal tauxSupplement) {

        return coutProduitHT.add(arrondi(coutProduitHT.multiply(tauxTva).divide(new BigDecimal(100))).add(arrondi(coutProduitHT.multiply(tauxSupplement).divide(new BigDecimal(100)))));

    }

    private static BigDecimal arrondi(BigDecimal arrondir) {
        //Le montant de chacune des taxes est arrondi aux 5 centimes supérieurs
        return arrondir.divide(new BigDecimal("0.05"), 0, RoundingMode.UP).multiply(new BigDecimal("0.05"));

    }


}