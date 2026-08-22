const { shareAll, withModuleFederationPlugin } = require('@angular-architects/module-federation/webpack');

// "Remote" de Module Federation responsable de las pantallas de Usuario Final:
// disponibilidad, nueva reserva y "mis reservas" (secc. 3.2). Solo expone sus rutas
// (lazy-loaded vía `loadChildren` en shell/src/app/app.routes.ts como 'mfClientes/Routes'),
// nunca componentes internos sueltos, para mantener acoplamiento mínimo con el shell y
// poder compilar/ejecutar este microfrontend de forma independiente (`ng serve` en :4201).
module.exports = {
  ...withModuleFederationPlugin({

    name: 'mfClientes',

    exposes: {
      './Routes': './src/app/app.routes.ts',
    },

    shared: {
      ...shareAll({ singleton: true, strictVersion: true, requiredVersion: 'auto' }),
    },

  }),

  output: {
    publicPath: 'http://localhost:4201/',
  },
};
