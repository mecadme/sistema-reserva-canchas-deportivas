const { shareAll, withModuleFederationPlugin } = require('@angular-architects/module-federation/webpack');

module.exports = {
  ...withModuleFederationPlugin({

    remotes: {
      "mfClientes": "http://localhost:4201/remoteEntry.js",
      "mfAdministracion": "http://localhost:4202/remoteEntry.js",
      "mfReportes": "http://localhost:4203/remoteEntry.js",
    },

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
