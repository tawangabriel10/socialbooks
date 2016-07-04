/* jshint camelcase: false */
'use strict';

var gulp = require('gulp'),
    gutil = require('gulp-util'),
    prefix = require('gulp-autoprefixer'),
    minifyCss = require('gulp-minify-css'),
    usemin = require('gulp-usemin'),
    uglify = require('gulp-uglify'),
    htmlmin = require('gulp-htmlmin'),
    //imagemin = require('gulp-imagemin'),
    ngAnnotate = require('gulp-ng-annotate'),
    ngConstant = require('gulp-ng-constant-fork'),
    jshint = require('gulp-jshint'),
    rev = require('gulp-rev'),
    proxy = require('proxy-middleware'),
    es = require('event-stream'),
    flatten = require('gulp-flatten'),
    del = require('del'),
    url = require('url'),
    wiredep = require('wiredep').stream,
    fs = require('fs'),
    runSequence = require('run-sequence'),
    browserSync = require('browser-sync');

var karmaServer = require('karma').Server;

var yeoman = {
    app: './',
    dist: './dist/',
    test: './test/spec/',
    tmp: '.tmp/',
    port: 9000,
    apiPort: 8080,
    geoServerPort: 8081,
    liveReloadPort: 35729
};

var endsWith = function (str, suffix) {
    return str.indexOf('/', str.length - suffix.length) !== -1;
};

var parseString = require('xml2js').parseString;
var parseVersionFromPomXml = function() {
    var version;
    var pomXml = fs.readFileSync('pom.xml', 'utf8');
    parseString(pomXml, function (err, result) {
        if (result.project.version && result.project.version[0]) {
            version = result.project.version[0];
        } else if (result.project.parent && result.project.parent[0] && result.project.parent[0].version && result.project.parent[0].version[0]) {
            version = result.project.parent[0].version[0]
        } else {
            throw new Error('pom.xml is malformed. No version is defined');
        }
    });
    return version;
};

gulp.task('clean', function (cb) {
    del([yeoman.dist], cb);
});

gulp.task('clean:tmp', function (cb) {
    del([yeoman.tmp], cb);
});

gulp.task('test', ['wiredep:test', 'ngconstant:dev'], function(done) {
    new karmaServer({
        configFile: __dirname + '/src/test/javascript/karma.conf.js',
        singleRun: true
    }, done).start();
});


gulp.task('copy', function() {
    return es.merge(
        gulp.src(yeoman.app + 'assets/**/*.{woff,svg,ttf,eot}').
        pipe(flatten()).
        pipe(gulp.dest(yeoman.dist + 'assets/fonts/')));
});

/*gulp.task('images', function() {
    return gulp.src(yeoman.app + 'assets/images/**').
    pipe(imagemin({optimizationLevel: 5})).
    pipe(gulp.dest(yeoman.dist + 'assets/images')).
    pipe(browserSync.reload({stream: true}));
});*/

gulp.task('styles', [], function() {
    return gulp.src(yeoman.app + 'assets/styles/**/*.css').
    pipe(gulp.dest(yeoman.tmp)).
    pipe(browserSync.reload({stream: true}));
});

gulp.task('serve', function() {
    runSequence('wiredep:test', 'wiredep:app', 'ngconstant:dev', function () {

        var proxyRoutesApi = [
            '/usuarios',
            '/livros'
        ];

        var proxies = createProxies(yeoman.apiPort, proxyRoutesApi);
        browserSync({
            open: true,
            port: yeoman.port,
            server: {
                baseDir: yeoman.app,
                middleware: proxies
            }
        });

        gulp.start('watch');
    });
});

var createProxies  = function(port, proxyRoutes) {
    var baseUri = 'http://localhost:' + port;

    var requireTrailingSlash = proxyRoutes.filter(function (r) {
        return endsWith(r, '/');
    }).map(function (r) {
        return r.substr(0, r.length - 1);
    });

    return [
        // Ensure trailing slash in routes that require it
        function (req, res, next) {
            requireTrailingSlash.forEach(function(route){
                if (url.parse(req.url).path === route) {
                    res.statusCode = 301;
                    res.setHeader('Location', route + '/');
                    res.end();
                }
            });

            next();
        }
    ].concat(
        // Build a list of proxies for routes: [route1_proxy, route2_proxy, ...]
        proxyRoutes.map(function (r) {
            var options = url.parse(baseUri + r);
            options.route = r;
            return proxy(options);
        }));
}

gulp.task('watch', function() {
    gulp.watch('bower.json', ['wiredep:test', 'wiredep:app']);
    gulp.watch(['gulpfile.js', 'pom.xml'], ['ngconstant:dev']);
    gulp.watch(yeoman.app + 'assets/styles/**/*.css', ['styles']);
    gulp.watch(yeoman.app + 'assets/images/**', ['images']);
    gulp.watch([yeoman.app + '*.html', yeoman.app + 'scripts/**', yeoman.app + 'i18n/**']).on('change', browserSync.reload);
});

gulp.task('wiredep', ['wiredep:test', 'wiredep:app']);

gulp.task('wiredep:app', function () {
    var s = gulp.src('src/main/resources/static/index.html')
        .pipe(wiredep({
            exclude: [/angular-i18n/]
        }))
        .pipe(gulp.dest('src/main/resources/static'));

    return s;
});

gulp.task('wiredep:test', function () {
    return gulp.src('src/test/javascript/karma.conf.js')
        .pipe(wiredep({
            exclude: [/angular-i18n/, /angular-scenario/],
            ignorePath: /\.\.\/\.\.\//, // remove ../../ from paths of injected javascripts
            devDependencies: true,
            fileTypes: {
                js: {
                    block: /(([\s\t]*)\/\/\s*bower:*(\S*))(\n|\r|.)*?(\/\/\s*endbower)/gi,
                    detect: {
                        js: /'(.*\.js)'/gi
                    },
                    replace: {
                        js: '\'{{filePath}}\','
                    }
                }
            }
        }))
        .pipe(gulp.dest('src/test/javascript'));
});

gulp.task('build', function () {
    runSequence('clean', 'copy', 'wiredep:app', 'ngconstant:prod', 'usemin');
});

gulp.task('usemin', function() {
    runSequence('images', 'styles', function () {
        return gulp.src([yeoman.app + '**/*.html', '!' + yeoman.app + 'bower_components/**/*.html']).
        pipe(usemin({
            css: [
                prefix.apply(),
                minifyCss({root: 'src/main/resources/static'}),  // Replace relative paths for static resources with absolute path with root
                'concat', // Needs to be present for minifyCss root option to work
                rev()
            ],
            html: [
                htmlmin({collapseWhitespace: true})
            ],
            js: [
                ngAnnotate(),
                uglify(),
                'concat',
                rev()
            ]
        })).
        pipe(gulp.dest(yeoman.dist));
    });
});

gulp.task('ngconstant:dev', function() {
    return ngConstant({
        dest: 'app.constants.js',
        name: 'suApp',
        deps:   false,
        noFile: true,
        interpolate: /\{%=(.+?)%\}/g,
        wrap: '/* jshint quotmark: false */\n"use strict";\n// DO NOT EDIT THIS FILE, EDIT THE GULP TASK NGCONSTANT SETTINGS INSTEAD WHICH GENERATES THIS FILE\n{%= __ngModule %}',
        constants: {
            ENV: 'dev',
            VERSION: '1.0.0'
        }
    })
        .pipe(gulp.dest(yeoman.app + 'scripts/app/'));
});

gulp.task('ngconstant:prod', function() {
    return ngConstant({
        dest: 'app.constants.js',
        name: 'Socialbooks',
        deps:   false,
        noFile: true,
        interpolate: /\{%=(.+?)%\}/g,
        wrap: '/* jshint quotmark: false */\n"use strict";\n// DO NOT EDIT THIS FILE, EDIT THE GULP TASK NGCONSTANT SETTINGS INSTEAD WHICH GENERATES THIS FILE\n{%= __ngModule %}',
        constants: {
            ENV: 'prod',
            VERSION: '1.0.0'
        }
    })
        .pipe(gulp.dest(yeoman.tmp + 'scripts/app/'));
});

gulp.task('jshint', function() {
    return gulp.src(['gulpfile.js', yeoman.app + 'scripts/**/*.js'])
        .pipe(jshint())
        .pipe(jshint.reporter('jshint-stylish'));
});

gulp.task('server', ['serve'], function () {
    gutil.log('The `server` task has been deprecated. Use `gulp serve` to start a server');
});

gulp.task('default', function() {
    runSequence('serve');
});
