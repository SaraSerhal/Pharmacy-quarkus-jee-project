## Objectifs du système à modéliser: 
On propose de modéliser un système de pharmacie pour la vente de médicaments.

Création et gestion des pharmacies
Les pharmaciens peuvent inscrire leur pharmacie et gérer les informations principales (adresse, horaires,...).
Mise à jour du stock des médicaments disponibles.

Recherche de médicaments
Les clients peuvent rechercher des médicaments par nom ou catégorie.
Filtrer les résultats selon la ville et vérifier la disponibilité en temps réel.

Vente de médicaments
 Les clients peuvent acheter des médicaments en ligne ou réserver pour un retrait.
 Validation des conditions d'achat (par exemple, ordonnance téléversée si nécessaire).
 
Commande et livraison
 Choix entre retrait en pharmacie ou livraison (si possible).
 Suivi des commandes : en préparation, prête, livrée.
 
Notifications
 Alertes pour les clients (commande prête, statut de livraison).
 Notifications pour les pharmaciens sur les nouvelles commandes,saturation de stock.

## Interfaces: 
administrateurs -> Système : Créer/Mise à jour des infos pharmacie
Système -> administrateurs : Confirmation (pharmacie créée/mise à jour)

administrateurs -> Système : Ajouter/Mise à jour stock médicaments
Système -> administrateurs : Confirmation (stock mis à jour)

Client-> Système : Rechercher médicaments (nom, catégorie, ville)
Système ->Client: Résultats (disponibilité, pharmacie)

Client-> Système : Passer commande (liste médicaments, mode de livraison)
Système -> Client: Confirmation commande (total, statut)

Client-> Système : Choisir livraison/retrait
Système -> Client: Notification (statut commande)

Système -> Client: Alerte (commande prête, livraison en cours)
Système -> administrateurs : Alerte (nouvelle commande, stock bas)



## Schéma relationnel :



## Exigences fonctionnelles
Le système DOIT permettre aux clients de rechercher des médicaments disponibles dans le stock.  
Le système NE DOIT proposer que les médicaments pour lesquels le stock est supérieur à zéro.  
Le système DOIT indiquer si un médicament nécessite une ordonnance avant l’achat.  
Le système DOIT permettre aux clients de télécharger une ordonnance pour valider l’achat des médicaments concernés.  
Le système DOIT permettre aux clients de créer un panier, de passer une commande, et de choisir un mode de livraison (retrait en pharmacie ou livraison à domicile).   
Le système DOIT permettre aux administrateurs (un pharmacien) de consulter et de mettre à jour les niveaux de stock des médicaments.  
Le système DOIT notifier les administrateurs lorsque le stock d’un médicament est critique.  
Le système DOIT permettre d’annuler une commande en cours et informer les clients de cette annulation.  

(à voir) checker si médicament dispo dans d’autres pharmacies aux alentours

## Exigences non fonctionnelles

Le système DOIT utiliser un messaging fiable pour garantir l’envoi des différentes notifications.   
Le système DOIT utiliser des microservices pour permettre une maintenance et une extension flexibles.
Le système Doit utiliser une gestion/architecture de base de données très robuste pour garantir une rapidité dans la lecture/traitement des données.
