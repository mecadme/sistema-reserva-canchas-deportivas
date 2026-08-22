const { shareAll, withModuleFederationPlugin } = require('@angular-architects/module-federation/webpack');

// Este es el "host" de Module Federation (Alcance Técnico secc. 4.1): el shell no
// contiene pantallas de negocio propias, solo layout, navegación y autenticación
// (ver src/app/core/auth), y carga en tiempo de ejecución los bundles remotos que
// publica cada microfrontend (mf-clientes, mf-administracion, mf-reportes) desde su
// propio remoteEntry.js. Cada URL apunta al puerto de `ng serve`/build de ese remote;
// no hay import estático entre proyectos, por eso cada uno se puede desplegar solo.
module.exports = {
  ...withModuleFederationPlugin({

    remotes: {
      "mfClientes": "http://localhost:4201/remoteEntry.js",
      "mfAdministracion": "http://localhost:4202/remoteEntry.js",
      "mfReportes": "http://localhost:4203/remoteEntry.js",
    },

    // shareAll({ singleton: true }) obliga a que Angular/RxJS/etc. se carguen una sola
    // vez para todo el conjunto de shell + remotes en el navegador; sin `singleton: true`
    // cada microfrontend traería su propia copia de Angular y se romperían cosas como
    // el router o la inyección de dependencias al cruzar el límite entre módulos.
    shared: {
      ...shareAll({ singleton: true, strictVersion: true, requiredVersion: 'auto' }),
    },

  }),

  output: {
    publicPath: 'http://localhost:4300/',
  },

  devServer: {
    historyApiFallback: true,
  },
};
