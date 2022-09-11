//region import
const path = require('path');
const fs = require('fs');
const customConfig = require('./custom.config.js');
const HtmlWebpackPlugin = require('html-webpack-plugin');
const { CleanWebpackPlugin } = require('clean-webpack-plugin');
const { VueLoaderPlugin } = require('vue-loader');
const { DefinePlugin } = require('webpack');
const TerserPlugin = require('terser-webpack-plugin');
const MiniCssExtractPlugin = require('mini-css-extract-plugin');
const CopyWebpackPlugin = require('copy-webpack-plugin');
//endregion

//region 搜索src/pages目录下所有文件，添加对应的入口点与HTML编译插件
const pageNames = fs.readdirSync(path.join(__dirname, 'src', 'pages'));

const entry = {};
const plugins = [];

pageNames.forEach(name => {
    entry[name] = path.join(__dirname, 'src', 'pages', name, name + '.js');
    plugins.push(new HtmlWebpackPlugin({
        template: path.join(__dirname, 'src', 'pages', name, name + '.html'),
        filename: name + '.html',
        chunks: [ name ]
    }));
});
//endregion

//region 根据环境添加环境变量
const env = process.env.NODE_ENV;
let envVariables = {};
const envVariablesFilePath = path.join(__dirname, 'env', env + '.json');
if(fs.existsSync(envVariablesFilePath)) {
    const jsonStr = fs.readFileSync(envVariablesFilePath)
        .toString('utf-8');
    envVariables = JSON.parse(jsonStr);
    for(let key in envVariables) {
        envVariables[key] = JSON.stringify(envVariables[key]);
    }
}
//endregion

//region devtool
let devtool;
if(process.env.NODE_ENV === 'development') {
    devtool = 'inline-source-map';
}
//endregion

const outputPath = path.join(__dirname, customConfig.outputDirName);

module.exports = {
    entry,
    output: {
        path: outputPath,
        filename: './static/js/[name]-[chunkhash].js'
    },
    module: {
        rules: [
            {
                test: /\.js$/,
                use: [
                    'babel-loader'
                ]
            },
            {
                test: /\.(sa|sc|c)ss$/,
                use: [
                    MiniCssExtractPlugin.loader,
                    'css-loader'
                ]
            },
            {
                test: /\.vue$/,
                use: [
                    'vue-loader'
                ]
            }
        ]
    },
    plugins: [
        ...plugins,
        new CleanWebpackPlugin(),
        new VueLoaderPlugin(),
        new DefinePlugin({
            'process.env': envVariables
        }),
        new MiniCssExtractPlugin({
            filename: './static/css/[name]-[chunkhash].css',
            chunkFilename: './static/css/[name]-[chunkhash].css'
        }),
        new CopyWebpackPlugin({
            patterns: [
                {
                    from: './public',
                    to: outputPath,
                    force: true,
                    noErrorOnMissing: true
                }
            ]
        })
    ],
    resolve: {
        extensions: [ '.js', '.css', '.vue' ],
        alias: {
            '@': path.join(__dirname, 'src')
        }
    },
    devServer: {
        port: customConfig.devServerPort,
        watchFiles: [ './src/**/*' ],
        hot: true
    },
    devtool,
    performance: {
        maxEntrypointSize: 2 * 1024 * 1024,
        maxAssetSize: 2 * 1024 * 1024
    },
    optimization: {
        minimize: true,
        minimizer: [
            new TerserPlugin({
                test: /\.js(\?.*)?$/i,
                parallel: true,
                terserOptions: {
                    output: {
                        comments: false
                    }
                },
                extractComments: true
            })
        ],
        splitChunks: {
            chunks: 'all',
            cacheGroups: {
                defaultVendors: {
                    test: /[\\/]node_modules[\\/]/
                },
            },
        }
    }
};
