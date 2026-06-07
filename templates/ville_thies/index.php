<?php
defined('_JEXEC') or die;
use Joomla\CMS\Factory;
use Joomla\CMS\Uri\Uri;

$app = Factory::getApplication();
$menu = $app->getMenu();
$active = $menu->getActive();
$isHome = ($active && $active->home && !isset($_GET['option']));
$doc = $app->getDocument();
$doc->addStyleSheet('https://fonts.googleapis.com/css2?family=Playfair+Display:ital,wght@0,600;0,700;1,600&family=Inter:wght@300;400;500;600&display=swap');
$doc->addStyleSheet('https://cdn.jsdelivr.net/npm/@tabler/icons-webfont@2.47.0/tabler-icons.min.css');
$doc->addStyleSheet(Uri::base(true) . '/templates/ville_thies/css/template.css');
?>

<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <jdoc:include type="head" />
</head>
<body class="site">

<nav>
    <div class="nav-brand">
        <div class="nav-logo"><i class="ti ti-building-community"></i></div>
        <div class="nav-name">Ville de Thiès <small>République du Sénégal</small></div>
    </div>
    <jdoc:include type="modules" name="navbar" style="none" />
</nav>

<jdoc:include type="message" />

<?php if ($isHome) : ?>

<div class="hero">
    <div>
        <div class="hero-badge">🚂 Cité du Rail — depuis 1904</div>
        <h1>Bienvenue à<br><span>Thiès,</span><br><em>notre commune</em></h1>
        <p class="hero-sub">Capitale régionale dynamique du Sénégal. Retrouvez vos démarches administratives, les projets et la vie de votre commune.</p>
        <div class="hero-btns">
            <a href="/ville_thies/index.php/services" class="btn-main">📄 Mes démarches</a>
            <a href="/ville_thies/index.php/municipalite" class="btn-sec">Découvrir la ville</a>
        </div>
    </div>
    <div class="hero-right">
        <div class="hero-img-card">
            <div class="hero-img-top" style="padding:0;height:180px">
    <img src="/ville_thies/templates/ville_thies/images/GareThies.png" 
         alt="Gare de Thiès" 
         style="width:100%;height:180px;object-fit:cover;display:block">
</div>
            <div class="hero-img-bottom">
                <div class="hib"><div class="hib-num">765K</div><div class="hib-lbl">Habitants</div></div>
                <div class="hib"><div class="hib-num">3</div><div class="hib-lbl">Arrondissements</div></div>
                <div class="hib"><div class="hib-num">1904</div><div class="hib-lbl">Fondation</div></div>
            </div>
            <div class="flag-strip">
                <span class="fs1"></span><span class="fs2"></span><span class="fs3"></span>
            </div>
        </div>
    </div>
</div>

<div class="section">
    <div class="sec-hd">
        <div class="sec-ttl">
            <div class="sec-chip">Services</div>
            <h2>Vos démarches en ligne</h2>
        </div>
        <a href="/ville_thies/index.php/services" class="sec-all">Voir tout →</a>
    </div>
    <div class="svc-grid">
        <div class="svc"><div class="svc-ico"><i class="ti ti-certificate"></i></div><h3>État civil</h3><p>Actes de naissance, mariage et décès</p></div>
        <div class="svc"><div class="svc-ico"><i class="ti ti-building"></i></div><h3>Urbanisme</h3><p>Permis de construire et autorisations</p></div>
        <div class="svc"><div class="svc-ico"><i class="ti ti-school"></i></div><h3>Bourses scolaires</h3><p>Aide à la scolarité des élèves</p></div>
        <div class="svc"><div class="svc-ico"><i class="ti ti-speakerphone"></i></div><h3>Doléances</h3><p>Signalez un problème dans votre quartier</p></div>
        <div class="svc"><div class="svc-ico"><i class="ti ti-heartbeat"></i></div><h3>Santé</h3><p>Centres de santé et vaccination</p></div>
        <div class="svc"><div class="svc-ico"><i class="ti ti-cash"></i></div><h3>Finances</h3><p>Taxes et redevances municipales</p></div>
    </div>
</div>

<div class="news-section">
    <div class="sec-hd">
        <div class="sec-ttl">
            <div class="sec-chip">Actualités</div>
            <h2>La vie de la commune</h2>
        </div>
        <a href="/ville_thies/index.php/actualites" class="sec-all">Toutes les actualités →</a>
    </div>
    <div class="news-grid">
        <div class="nc">
            <div class="nc-top"><div><div class="nc-date">02</div><div class="nc-mo">Juin 2026</div></div><div class="nc-cat">Développement</div></div>
            <div class="nc-body"><div class="nc-title">Inauguration du marché central rénové</div><div class="nc-exc">Le maire Dr Babacar Diop a ouvert les portes après 18 mois de travaux.</div></div>
        </div>
        <div class="nc">
            <div class="nc-top"><div><div class="nc-date">28</div><div class="nc-mo">Mai 2026</div></div><div class="nc-cat">Environnement</div></div>
            <div class="nc-body"><div class="nc-title">5 000 arbres plantés dans la commune</div><div class="nc-exc">L'objectif de reboisement 2026 atteint dans les trois arrondissements.</div></div>
        </div>
        <div class="nc">
            <div class="nc-top"><div><div class="nc-date">15</div><div class="nc-mo">Mai 2026</div></div><div class="nc-cat">Voirie</div></div>
            <div class="nc-body"><div class="nc-title">Réfection des routes — Médina Fall</div><div class="nc-exc">Les travaux débutent le 10 juin pour une durée de 3 semaines.</div></div>
        </div>
    </div>
</div>

<div class="cta-band">
    <div>
        <h3>Besoin d'aide ? La mairie vous répond.</h3>
        <p>115, Place de France · +221 33 951 12 91 · mairie@thies.sn · Lundi–Vendredi 8h–16h</p>
    </div>
    <a href="/ville_thies/index.php/contact" class="btn-white">Prendre rendez-vous</a>
</div>

<?php else : ?>

<div class="section">
    <jdoc:include type="component" />
</div>

<?php endif; ?>

<footer>
    <div>
        <div class="ft-brand">Ville de Thiès <small>Commune — République du Sénégal</small></div>
        <p class="ft-desc">La mairie de Thiès œuvre chaque jour pour le bien-être de ses citoyens et le rayonnement de la Cité du Rail.</p>
    </div>
    <div class="ft-col">
        <h4>Navigation</h4>
        <a href="/ville_thies/">Accueil</a>
        <a href="/ville_thies/index.php/municipalite">Municipalité</a>
        <a href="/ville_thies/index.php/services">Services</a>
        <a href="/ville_thies/index.php/actualites">Actualités</a>
        <a href="/ville_thies/index.php/contact">Contact</a>
    </div>
    <div class="ft-col">
        <h4>Informations</h4>
        <a href="#">Mentions légales</a>
        <a href="#">Accessibilité</a>
        <a href="#">Plan du site</a>
        <a href="#">Appels d'offres</a>
    </div>
</footer>
<div class="ft-bottom">
    <span>© 2026 Mairie de la Ville de Thiès — Tous droits réservés</span>
    <span>Réalisé avec Joomla 5 CMS</span>
</div>

<jdoc:include type="modules" name="debug" />

</body>
</html>